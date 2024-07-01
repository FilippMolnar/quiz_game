package server.api;

import commons.*;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.ScoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(path = "/api")
public class GameController {

    private ScoreRepository scoreRepository;

    private final Map<Integer, Game> games = new HashMap<>();
    private final Map<String, Game> socketToGame = new HashMap<>();


    private final SimpMessageSendingOperations simpMessagingTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

    public GameController(ScoreRepository scoreRepository, SimpMessageSendingOperations simpMessagingTemplate) {
        this.scoreRepository = scoreRepository;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Create a new game
     * @param gameID the ID of the new game
     */
    public void addNewGame(int gameID) {
        games.put(gameID, new Game(gameID));
    }

    /**
     * Adds a new player to given game
     * @param gameID the ID of the game
     * @param player the player to be added to game
     */
    public void addPlayerToGame(int gameID, Player player) {
        if (games.get(gameID) == null) {
            addNewGame(gameID);
        }
        games.get(gameID).addPlayer(player);
        socketToGame.put(player.getSocketID(), getGame(gameID));
    }

    /**
     * Get game instance with given socket
     * @param socketID the socketID of the game
     * @return the game corresponding to this socket
     */
    public Game getGameFromSocket(String socketID) {
        return socketToGame.get(socketID);
    }

    /**
     * Remove a player from a game
     * @param gameID the ID of the game
     * @param player the player to be removed
     */
    public void removePlayer(int gameID, Player player) {
        games.get(gameID).removePlayer(player);
    }

    /**
     * Get a game by its ID
     * @param gameID ID of the game
     * @return the game corresponding to this ID
     */
    public Game getGame(int gameID) {
        return games.get(gameID);
    }


    /**
     * Retrieve the leaderboard for the current game
     *
     * @param gameID identifier for the current game
     * @return a list of pairs of score and player sorted in descending order by their score
     */
    @GetMapping(path = "/game/leaderboard/{gameID}")
    public Map<Integer,List<String>> getLeaderboard(@PathVariable("gameID") int gameID) {
        Game cur = getGame(gameID);

        return cur.getLeaderboard();
    }

    /**
     * Set a player`s score in the given game
     * @param gameID the ID of the given game
     * @param pair Pair with the player and his new score
     */
    @PostMapping(path = "/game/score/{gameID}")
    public void setScore(@PathVariable("gameID") int gameID, Pair<Player, Integer> pair) {
        Game cur = getGame(gameID);
        Player player = pair.getLeft();
        int score = pair.getRight();
        cur.setScore(player.getName(), score);
    }

    /**
     * Request to get a game instance corresponding to this ID
     * @param gameID the ID of the game to be retrieved
     * @return the game corresponding to this ID
     */
    @GetMapping(path = "/game/getGame/{gameID}")
    public Game getGameMapping(@PathVariable("gameID") int gameID) {
        return getGame(gameID);
    }

    /**
     * Request to get all questions for a given game
     * @param gameID the ID of the game
     * @return List of questions in the game
     */
    @GetMapping(path = "/game/getQuestions/{gameID}")
    public List<Question> getGameQuestions(@PathVariable("gameID") int gameID) {
        Game currentGame = getGame(gameID);
        return currentGame.getQuestions();
    }

    /**
     * Request to get the leaderboard for singleplayer
     * @return A list of scores in singleplayer leaderboard
     */
    @GetMapping("api/game/getSingleLeaderboard")
    public List<Score> getSingleLeaderboard() {
        return scoreRepository.getLeaderboard();
    }

    /**
     * Request to send his reaction
     * @param ur the reaction to be sent
     */
    @MessageMapping("/reactions")
    public void userReact(@Payload UserReaction ur) {
        int gameID = ur.getGameID();
        Game current = this.getGame(gameID);
        var playerList = current.getPlayers();
        for (Player player : playerList) {
            String playerID = player.getSocketID();
            LOGGER.info("Sending reaction "+ ur);
            simpMessagingTemplate.convertAndSendToUser(playerID, "queue/reactions", ur);
            LOGGER.info("Sent reaction event "+ur+" to "+player.getName());
        }
    }

    /**
     * When a user has chosen an option, they send their answer to the server. Once all players have answered, the server
     * sends everyone the number of players who have chosen each option. This functionality should only apply for the two
     * types of multiple choice questions.
     * @param a - the answer
     *          When everyone has answered (or the time has run out), each client gets a List of 3 Integers where the 0 index corresponds
     *          to the number of players who have chosen A, 1 -> B and 2 -> C.
     */
    @MessageMapping("/submit_answer")
    public void submitAnswer(@Payload Answer a) {
        int gameID = a.getGameID();
        Game current = this.getGame(gameID);
        LOGGER.info("Receiving option " + a.getOption() + " for game ID " + gameID +
                " with username " + a.getName() + " correct:" + a.isCorrect());
        LOGGER.info("Full answer:"  + a);
        current.updateScore(a.getName(), a.getScore());
        LOGGER.info("Game with " + gameID + " has " + (current.getRequested() + 1) + " answers and "
                + current.getPlayersInGame() + " total players");
        if (current.newRequest(a.getOption())) {
            List<Integer> options = current.getOptionsStatistics();
            var playerList = current.getPlayers();
            LOGGER.info("Sending results: " + options + " to game ID " + gameID);
            for (Player player : playerList) {
                String playerID = player.getSocketID();
                simpMessagingTemplate.convertAndSendToUser(playerID, "queue/statistics", options);
            }
            current.resetOptions();
            LOGGER.info("================");
        }
    }
}
