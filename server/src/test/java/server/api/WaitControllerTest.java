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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class WaitControllerTest {

    private WaitController waitController;
    private final List<Player> lobby = new ArrayList<>();

    private final SimpMessagingTemplate mockSimpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);

    private final QuestionController questionController = Mockito.mock(QuestionController.class);

    private GameController mockGameController;
    private final Player player1 = new Player("player1");
    private final Player player2 = new Player("player2");
    private final Player player3 = new Player("player3");

    @BeforeEach
    public void setup() {
        lobby.clear();

        reset(mockSimpMessagingTemplate);
        mockGameController = new GameController(null, mockSimpMessagingTemplate);
        waitController = new WaitController(mockSimpMessagingTemplate, mockGameController, questionController);
    }

    @Test
    public void addsNameTest() {
        waitController.addNamePost(new Player("Name"));
        lobby.add(new Player("Name"));
        assertEquals(lobby, waitController.getLobbyPlayers());
    }

    @Test
    public void remove2PlayersTest() {
        waitController.addNamePost(player1);
        waitController.addNamePost(player2);
        waitController.addNamePost(player3);

        waitController.playerDisconnectWaitingRoom(player2); // [1,2,3] - [2] = [1,3]
        assertEquals(waitController.getLobbyPlayers(), List.of(player1, player3));

        waitController.playerDisconnectWaitingRoom(player1);// [1,3] - 1 = [3]
        assertEquals(waitController.getLobbyPlayers(), List.of(player3));
    }

    @Test
    public void removeInexistentPlayer() {
        waitController.addNamePost(player1);

        waitController.playerDisconnectWaitingRoom(player2);
        assertEquals(waitController.getLobbyPlayers(), List.of(player1));
    }

    @Test
    public void checkSocketCalledAfterPostRequest() {
        waitController.addNamePost(new Player("Name"));
        verify(mockSimpMessagingTemplate,times(1)).convertAndSend(
                anyString(),eq(new Player("Name")));
    }

    @Test
    public void get20RandomMostLeastQuestions(){
        var list = waitController.get20RandomMostLeastQuestions();
        assertEquals(list.size(),20, "List should have 20 elements");
        verify(questionController,times(20)).getTypeMostLeast();
    }

    @Test
    public void startGame(){
        waitController.addNamePost(player1);
        waitController.addNamePost(player2);
        waitController.addNamePost(player3);

        waitController.startGame();
        assertEquals(player1.getGameID(),0, "Player should be in game id 0 ");
        assertEquals(1,waitController.getGameID(),
                "The next game id should be 1");
        verify(mockSimpMessagingTemplate,times(3))
                .convertAndSendToUser(anyString(), eq("queue/startGame/gameID"),any(List.class));
    }

    @Test
    public void addNameSockets(){
        waitController.addNameSockets(player1, () -> "My-socket-id");
        assertEquals(player1.getSocketID(), "My-socket-id");
        assertTrue(waitController.getLobbyPlayers().contains(player1),
                "The player should be in the waiting room");
    }

    @Test
    public void coverHands(){
        waitController.addNamePost(player1);
        waitController.coverHands(player1);
        verify(mockSimpMessagingTemplate,times(0)).convertAndSendToUser(
                anyString(),anyString(),any());
    }

    @Test
    public void coverInk(){
        waitController.addNamePost(player1);
        waitController.addNamePost(player2);
        waitController.coverInk(player1);
        verify(mockSimpMessagingTemplate,times(1)).convertAndSendToUser(
                anyString(),eq("queue/cover_ink/gameID"),eq(0)); // game ID 0
    }

    @Test
    public void decreaseTime(){
        waitController.addNamePost(player1);
        waitController.startGame();
        // game id is 1
        waitController.addNamePost(player1);
        waitController.addNamePost(player2);
        waitController.addNamePost(player3);
        waitController.startGame();

        reset(mockSimpMessagingTemplate);
        waitController.decreaseTime(player1);
        verify(mockSimpMessagingTemplate,times(2)).convertAndSendToUser(
                anyString(),eq("queue/decrease_time/gameID"),eq(1));
    }

    @Test
    public void disconnectFromNonexistentGame(){
        waitController.disconnectFromGame(player1);
        // nothing should break here
    }

    @Test
    public void removingPlayerFromGame(){
        waitController.addNameSockets(player1, () -> "Socket-id-1");
        waitController.addNameSockets(player2, () -> "Socket-id-2");

        waitController.startGame();

        System.out.println(mockGameController.getGame((int)player1.getGameID()).getPlayers());

        assertEquals(mockGameController.getGameFromSocket("Socket-id-1").getPlayersInGame(),2,
                "Initially there are 2 players in the game");
        waitController.disconnectFromGame(player1);
        assertEquals(mockGameController.getGameFromSocket("Socket-id-1").getPlayersInGame(),1,
                "There should be only one player left in the game");
    }

    public void handleWaitingRoomSocketDisconnect(){
        // this should be called when a player looses internet connection or just kills the app
//        waitController.handleSessionDisconnect(new SessionDisconnectEvent(, , ))
    }
    @Test
    public void testWaitingRoomEmpty(){
        waitController.addNameSockets(player1, () -> "Socket-id-1");
        waitController.addNameSockets(player2, () -> "Socket-id-2");

        waitController.startGame();
        assertEquals(waitController.getLobbyPlayers().size(),0,"No more players in waiting room");

    }

    @Test
    public void testWaitingThreePeople(){
        assertEquals(waitController.getLobbyPlayers().size(),0,"No more players in waiting room");
        waitController.addNameSockets(player1,() -> "Socket-id-1");
        waitController.addNameSockets(player2,() -> "Socket-id-2");
        waitController.addNameSockets(player3,() -> "Socket-id-3");

        assertEquals(waitController.getLobbyPlayers().size(),3,"3 players in waiting room");
        waitController.playerDisconnectWaitingRoom(player1);
        assertEquals(waitController.getLobbyPlayers().size(),2,"2 players in waiting room");

        waitController.startGame();
        // player2,player3

        Game game = mockGameController.getGameFromSocket("Socket-id-3");
        // the game with player1,
        assertEquals(game.getPlayersInGame(),2,
                "There should be only 2 players left in the game: p2 and p3");
        waitController.disconnectFromGame(player1);
        assertEquals(game.getPlayersInGame(),2,
                "There should still be only 2 players left in the game: p2 and p3");
        waitController.disconnectFromGame(player2);
        assertEquals(game.getPlayersInGame(),1,
                "There should still be only 1 players left in the game: p3");
    }
}
