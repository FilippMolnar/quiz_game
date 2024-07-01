/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package server.api;

import commons.Game;
import commons.Player;
import commons.Question;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import server.Utils;

import java.security.Principal;
import java.util.*;


@RestController
@RequestMapping("/api/wait")
public class WaitController {

    private final SimpMessageSendingOperations simpMessagingTemplate;
    private final List<Player> lobbyPlayers = new ArrayList<>();
    private final Utils utils;

    private final Logger LOGGER = LoggerFactory.getLogger(WaitController.class);
    private final GameController gameController;
    private final QuestionController questionController;

    @Getter
    private int gameID = 0;


    WaitController(SimpMessageSendingOperations simpMessagingTemplate, GameController gameController, QuestionController questionController) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.utils = new Utils(simpMessagingTemplate);
        this.gameController = gameController;
        this.questionController = questionController;
    }

    /**
     * Adds the player to the current player list in the lobby and then pushes it to the socket channel
     *
     * @param player Player that is sent in the request
     */
    @PostMapping(path = {"", "/"})
    public void addNamePost(@RequestBody Player player) {
        lobbyPlayers.add(player);
        player.setGameID(gameID);
        simpMessagingTemplate.convertAndSend("/topic/waitingRoom", player);
        List<String> playerNames = lobbyPlayers.stream().map(Player::getName).toList();
        LOGGER.info("Players in waiting room are:" + playerNames);
        addPlayerToGameID(player);
    }

    /**
     * Gets a list of 20 randomly chosen questions
     * It consists of 8 questions of type equal, 4 estimate questions and 8 of type most
     * @return The list of random questions
     */
    @GetMapping("/getRandomQuestions")
    public List<Question> getRandomQuestionTypes() {
        // 0 -> equal energy
        // 1 -> highest energy
        // 2 -> estimate answer
        final int nrEqual = 8;
        final int nrEstimate = 4;
        final int nrHighest = 8;
        List<Question> list = new ArrayList<>();
        for (int i = 0; i < nrEqual; i++)
            list.add(questionController.getTypeEqual());
        for (int i = 0; i < nrHighest; i++)
            list.add(questionController.getTypeMostLeast());
        for (int i = 0; i < nrEstimate; i++)
            list.add(questionController.getTypeEstimate());
        Collections.shuffle(list);
        return list;
    }

    /**
     * Get 20 totally random questions not considering their type
     * @return A List of 20 random questions
     */
    public List<Question> get20RandomMostLeastQuestions() {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < 20; i++)
            questions.add(questionController.getTypeMostLeast());
        return questions;
    }


    /**
     * Post mapping from a player to <b>Start</b> the game
     * Sends a message to start the game to all players
     */
    @PostMapping(path = {"", "/start"})
    public void startGame() {

        Game currentGame = gameController.getGame(gameID);
        LOGGER.info("Starting game with id " + gameID + " with " + currentGame.getPlayers().size() + " players");

        lobbyPlayers.clear();
        var playerList = currentGame.getPlayers();
        if (playerList == null) {
            LOGGER.error("There are no players in the waiting room, but POST is called!");
            return;
        }

        var questionList = getRandomQuestionTypes();
        currentGame.setQuestions(questionList);
        utils.sendToAllPlayers(playerList, "queue/startGame/gameID", gameID);
        gameID++;
    }

    /**
     * Adds the player to the current game
     * @param player The player which should be added to game
     */
    public void addPlayerToGameID(Player player) {
        gameController.addPlayerToGame(gameID, player);
    }

    /**
     * Adds the player to the waiting room
     * @param player The player to be added to waiting room
     * @param principal
     */
    @MessageMapping("/enterRoom")
    public void addNameSockets(@Payload Player player, Principal principal) {
        LOGGER.info("add player with name " + player.getName() + " to the waiting room(gameID = "
                + gameID + " with sockets. The player's id is " + principal.getName());
        player.setSocketID(principal.getName());
        addNamePost(player);
    }

    /**
     * Get mapping from client to get all players in the lobby
     * @return list of players in lobby
     */
    @GetMapping(path = {"", "/"})
    public List<Player> getLobbyPlayers() {
        return lobbyPlayers;
    }

    /**
     * Handler for player disconnection
     * @param event given by server on player disconnection
     */
    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor headers = StompHeaderAccessor.wrap(event.getMessage());
        if (headers.getUser() == null) {
            LOGGER.error("The socket disconnect event did not have a socket id configured.This should probably not happen!");
            return;
        }
        String socketID = headers.getUser().getName();
        Game game = gameController.getGameFromSocket(socketID);
        if (game == null) {
            LOGGER.warn("Socket message: Game is null meaning that the player is not in the game right now");
            return;
        }
        var optionalPlayer = game.getPlayers().stream().filter(p -> p.getSocketID().equals(socketID)).findFirst();
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            game.removePlayer(player);
            System.out.println("Socket id of the found player is : " + player.getSocketID());
            boolean removed = lobbyPlayers.removeIf(p ->
                p.getSocketID().equals(player.getSocketID()) && p.getName().equals(player.getName())
            );
            if (removed) {
                LOGGER.info("Socket message: Player " + player.getName() + " disconnected because he closed the socket!");
                simpMessagingTemplate.convertAndSend("/topic/disconnect", player);
            } else {
                LOGGER.info("Socket message: Player " + player.getName() + " disconnected from the game");
            }
        } else {
            LOGGER.error("There is no player with this socket ID in the game" + game.getGameID());
        }
    }

    /**
     * Request from client to disconnect the player from the game
     * @param player The player who disconnects
     */
    @MessageMapping("/disconnectFromGame")
    public void disconnectFromGame(Player player) {
        LOGGER.info("Receiving : " + player);
        int goodGameId = (int)player.getGameID();
        Game game = gameController.getGame(goodGameId);
        if (game == null) {
            LOGGER.error("Remove exit button for " + player.getName() + " failed because game is null");
        } else {
            game.removePlayer(player);
            LOGGER.info("Remove exit button: " + player.getName() + " from the game with id " + game.getGameID());
        }
    }

    /**
     * Request from client to disconnect player from waiting room
     * @param player The player to be disconnected
     */
    @MessageMapping("/disconnect")
    public void playerDisconnectWaitingRoom(Player player) {
        int gameID = (int)player.getGameID();
        Game game = gameController.getGame(gameID);
        if (lobbyPlayers.remove(player)) {
            game.removePlayer(player);
            LOGGER.info("Manual remove waiting room: " + player.getName() + " succeeded!");
            simpMessagingTemplate.convertAndSend("/topic/disconnect", player);
        } else {
            List<String> playerNames = lobbyPlayers.stream().map(Player::getName).toList();
            LOGGER.error("Manual remove waiting room for " + player.getName() + " fail, he is not in the lobby players: " + playerNames);
        }
    }

    /**
     * Request from client to decrease time for players
     * @param player The player who requests time decrease
     */
    @MessageMapping("/decrease_time")
    public void decreaseTime(Player player) {
        int gid = (int)player.getGameID();
        Game currentGame = gameController.getGame(gid);
        var playerList = currentGame.getPlayers();
        sendToAllOtherUsers(playerList,"queue/decrease_time/gameID", gid, player);
    }

    /**
     * Request from client to increase the player`s time in order to google
     * @param player The player who googles
     */
    @MessageMapping("/increase_time")
    public void googling(Player player)
    {
        int gid = (int)player.getGameID();
        Game currentGame = gameController.getGame(gid);
        var playerList = currentGame.getPlayers();
        sendToUser(playerList,"/queue/increase_time/gameID", gid, player);
    }

    /**
     * Request from client to show cover hands to his opponents
     * @param player The player who used cover hands joker
     */
    @MessageMapping("/cover_hands")
    public void coverHands(Player player) {
        int gid = (int)player.getGameID();
        Game currentGame = gameController.getGame(gid);
        var playerList = currentGame.getPlayers();
        sendToAllOtherUsers(playerList,"queue/cover_hands/gameID", gid, player);
    }

    /**
     * Request from client to perform barrel roll to opponents
     * @param player The player who used barrel roll joker
     */
    @MessageMapping("/barrel_roll")
    public void barrelRoll(Player player) {
        int gid = (int)player.getGameID();
        Game currentGame = gameController.getGame(gid);
        var playerList = currentGame.getPlayers();
        sendToAllOtherUsers(playerList,"queue/barrel_roll/gameID", gid, player);
    }

    /**
     * Request from client to show ink to opponents` screens
     * @param player The player who used cover ink joker
     */
    @MessageMapping("/cover_ink")
    public void coverInk(Player player) {
        int gid = (int)player.getGameID();
        Game currentGame = gameController.getGame(gid);
        var playerList = currentGame.getPlayers();
        sendToAllOtherUsers(playerList,"queue/cover_ink/gameID", gid, player);
    }

    /**
     * Send info through socket to all players in a game except the one requesting it
     * @param playerList The list of players to whom info should be sent
     * @param destination The socket location
     * @param gID GameID
     * @param player The player who requests to send info
     */
    public void sendToAllOtherUsers(Set<Player> playerList, String destination, int gID, Player player){
        if(playerList == null) return;
        for (Player p : playerList) {
            String playerID = p.getSocketID();
            if(player.getName().equals(p.getName())) continue;
            simpMessagingTemplate.convertAndSendToUser(playerID, destination, gID);
        }
    }

    /**
     * Send info to the user who requested it
     * @param destination socket location
     * @param gID GameID
     * @param player The player who requested to send info
     */
    public void sendToUser(Set<Player> playerList, String destination, int gID, Player player)
    {
        if(playerList == null) return;
        for (Player p : playerList) {
            String playerID = p.getSocketID();
            if(player.getName().equals(p.getName()))
            simpMessagingTemplate.convertAndSendToUser(playerID, destination, gID);
        }
    }
}
