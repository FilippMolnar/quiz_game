package server.api;

import commons.*;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import server.database.ScoreRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameControllerTest {
    SimpMessagingTemplate simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
    ScoreRepository scoreRepository = Mockito.mock(ScoreRepository.class);

    GameController gameController = new GameController(scoreRepository, simpMessagingTemplate);
    Player p1 = new Player("First");
    Player p2 = new Player("Second");
    Player p3 = new Player("Third");


    @BeforeEach
    void setup() {
        reset(simpMessagingTemplate);
        gameController.addNewGame(1);
        p1.setSocketID("id1");
        p2.setSocketID("id2");
    }
    @Test
    void addNewGame() {
        assertNotNull(gameController.getGame(1));
    }

    @Test
    void addPlayerToGame() {
        gameController.addPlayerToGame(1,p1);
        Game game = gameController.getGame(1);
        Set<Player> s = game.getPlayers();
        assertTrue(s.contains(p1));
    }
    @Test
    void addPlayerToEmptyGameID(){
        int gameID = 10;
        gameController.addPlayerToGame(gameID,p1);
        Game game = gameController.getGame(gameID);
        Set<Player> s = game.getPlayers();
        assertTrue(s.contains(p1));
    }
    @Test
    public void userReaction(){
        gameController.addPlayerToGame(1,p1);
        gameController.addPlayerToGame(1,p2);
        gameController.addPlayerToGame(1,p3);
        UserReaction reaction = new UserReaction(1, "nick","happy");
        gameController.userReact(reaction);
        // call the sendUser to all players in game (p1,p2,p3) once
        for(Player player : gameController.getGame(1).getPlayers()) {
            verify(simpMessagingTemplate).
                    convertAndSendToUser(eq(player.getSocketID()), anyString(), eq(reaction));
        }
    }

    @Test
    void setScore() {
        gameController.addPlayerToGame(1,p1);
        gameController.setScore(1, Pair.of(p1, 50));
        Game game = gameController.getGame(1);
        assertEquals(50, game.getScore(p1));
    }

    @Test
    void removePlayer() {
        gameController.removePlayer(1,p2);
        gameController.addPlayerToGame(1,p1);
        Game game = gameController.getGame(1);
        assertFalse(game.getPlayers().contains(p2));
        gameController.removePlayer(1,p1);
        game = gameController.getGame(1);
        assertNull(game.getPlayers());
    }

    @Test
    void submitAnswer(){
        gameController.addPlayerToGame(1,p1);
        gameController.addPlayerToGame(1,p2);
        gameController.addPlayerToGame(1,p3);
        Game game = gameController.getGame(1);

        gameController.submitAnswer(new Answer(true,"optionA",1,120,p1.getName()));
        verify(simpMessagingTemplate,times(0))
                .convertAndSendToUser(any(), any(), any()); // still wait for the other players to answer
        // we just have 2 players
        gameController.submitAnswer(new Answer(true,"optionA",1,120,p1.getName()));
        verify(simpMessagingTemplate,times(0))
                .convertAndSendToUser(any(), any(), any()); // still wait for the other players to answer
        // we have [3,0,0] as options
        gameController.submitAnswer(new Answer(true,"optionA",1,120,p1.getName()));
        verify(simpMessagingTemplate,times(3))
                .convertAndSendToUser(any(), any(), eq(List.of(3,0,0))); // still wait for the other players to answer
    }

    @Test
    void getGame() {
        assertNull(gameController.getGame(2));
        assertNotNull(gameController.getGame(1));
    }

    @Test
    void getLeaderboard() {
        gameController.setScore(1,Pair.of(p1,50));
        gameController.addPlayerToGame(1,p1);
        gameController.setScore(1,Pair.of(p2,100));
        Map<Integer, List<String>> leaderboard = gameController.getLeaderboard(1);
        for (Integer integ : leaderboard.keySet()) {
            System.out.println(integ);
            for (String s : leaderboard.get(integ)) {
                System.out.println(s);
            }
        }
        assertEquals(p1.getName(), leaderboard.get(50).get(0));
        assertEquals(p2.getName(), leaderboard.get(100).get(0));
    }

    @Test
    void testSetScore() {
        Pair<Player,Integer> pl = Pair.of(p2,50);
        gameController.setScore(1,pl);
        Game game = gameController.getGame(1);
        assertEquals(game.getScore(p2),50);
    }

    @Test
    void getGameMapping() {
        assertNotNull(gameController.getGameMapping(1));
    }

    @Test
    void testGetGameQuestions()
    {
        Question q = new Question(new Activity(),new ArrayList<>(), QuestionType.Estimate);
        Game game = gameController.getGame(1);
        List<Question> ls = new ArrayList<>();
        ls.add(q);
        game.setQuestions(ls);
        assertEquals(ls,gameController.getGameQuestions(1));
    }

    @Test
    void testGetGameFromSocket(){
        p1.setSocketID("sockID");
        gameController.addPlayerToGame(10,p1);
        assertEquals(gameController.getGameFromSocket("sockID")
                ,gameController.getGame(10),"I should get game with id 10");
    }
    @Test
    void testGetSingleLeaderboard(){
        assertNotNull(gameController.getSingleLeaderboard());
    }
}
