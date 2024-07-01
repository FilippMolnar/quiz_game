package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    Game game = new Game(1);
    Player p = new Player("play");
    Player p1 = new Player("pl");
    @BeforeEach
    void setUp()
    {
        game.addPlayer(p);
        game.addPlayer(p1);
    }
    @Test
    void constructorTest() {
        Game g = new Game();
        assertEquals(g.getOptionsStatistics().get(0),0);
        assertEquals(g.getOptionsStatistics().get(1),0);
        assertEquals(g.getOptionsStatistics().get(2),0);
        assertNotNull(game);
    }

    @Test
    void testGetPlayers() {
        assertEquals(game.getPlayersInGame(),2);
    }

    @Test
    void getRequested() {
        assertEquals(game.getRequested(),0);
    }

    @Test
    void setRequested() {
        game.setRequested(5);
        assertEquals(game.getRequested(),5);
    }

    @Test
    void newRequest() {
        game.newRequest("option");
        assertEquals(game.getRequested(),1);
        assertTrue(game.newRequest(""));
    }

    @Test
    void resetOptions() {
        Game g = new Game();
        g.resetOptions();
        assertEquals(g.getOptionsStatistics().get(0),0);
        assertEquals(g.getOptionsStatistics().get(1),0);
        assertEquals(g.getOptionsStatistics().get(2),0);
    }

    @Test
    void getGameID() {
        assertEquals(game.getGameID(),1);
    }

    @Test
    void setGameID() {
        game.setGameID(2);
        assertEquals(game.getGameID(),2);
    }

    @Test
    void getQuestions() {
        List<Question> ls = new ArrayList<>();
        Question q = new Question(new Activity(),new ArrayList<>(),QuestionType.EqualEnergy);
        ls.add(q);
        assertTrue(game.getQuestions().size()==0);
        game.setQuestions(ls);
        assertEquals(game.getQuestions(),ls);
    }

    @Test
    void setQuestions() {
        List<Question> ls = new ArrayList<>();
        Question q = new Question(new Activity(),new ArrayList<>(),QuestionType.EqualEnergy);
        Question q1 = new Question(new Activity(),new ArrayList<>(),QuestionType.Estimate);
        ls.add(q);
        ls.add(q1);
        game.setQuestions(ls);
        assertEquals(game.getQuestions(),ls);
        Question q2 = new Question(new Activity(),new ArrayList<>(),QuestionType.HighestEnergy);
        ls.add(q2);
        game.setQuestions(ls);
        assertEquals(game.getQuestions(),ls);
    }

    @Test
    void addPlayer() {
        assertTrue(game.getPlayers().contains(p));
        assertTrue(game.getPlayers().contains(p1));
    }

    @Test
    void removePlayer() {
        game.removePlayer(p1);
        assertFalse(game.getPlayers().contains(p1));
        assertTrue(game.getPlayers().contains(p));
    }

    @Test
    void getPlayers() {
        Game g = new Game();
        assertNull(g.getPlayers());
        assertTrue(game.getPlayers().size()==2);
    }

    @Test
    void setPlayers() {
        Set<Player> st = new HashSet<Player>();
        Player player = new Player("new");
        st.add(player);
        game.setPlayers(st);
        assertEquals(game.getPlayers(),st);
    }

    @Test
    void getLeaderboard() {
        game.setScore(p1.getName(),50);
        game.setScore(p.getName(),100);
        Map<Integer, List<String>> leaderboard = game.getLeaderboard();
        Map<Integer, List<String>> l = new HashMap<>();

        l.put(0, new ArrayList<>(Arrays.asList("play", "pl")));
        l.put(50, new ArrayList<>(Arrays.asList("pl")));
        l.put(100, new ArrayList<>(Arrays.asList("play")));
        assertEquals(leaderboard, l);
    }

    @Test
    void setScore() {
        game.setScore(p.getName(),100);
        game.setScore(p1.getName(),100);
        assertEquals(game.getScore(p),100);
        assertEquals(game.getScore(p1),100);

    }

    @Test
    void getScore() {
        game.setScore(p.getName(),100);
        game.setScore(p1.getName(),75);

        assertEquals(game.getScore(p),100);
        assertEquals(game.getScore(p1),75);
    }

    @Test
    void updateScore() {
        game.updateScore(p.getName(),5);
        game.updateScore(p1.getName(),5);

        assertEquals(game.getScore(p),5);
        assertEquals(game.getScore(p1),5);
    }

    @Test
    void getQuestion() {
        List<Question> ls = new ArrayList<>();
        Question q = new Question(new Activity(),new ArrayList<>(),QuestionType.EqualEnergy);
        Question q1 = new Question(new Activity(),new ArrayList<>(),QuestionType.Estimate);
        ls.add(q);
        ls.add(q1);
        game.setQuestions(ls);

        assertEquals(game.getQuestion(),q);
        game.incrementQNum();
        assertEquals(game.getQuestion(),q1);
    }

    @Test
    void printLeaderBoardTest() {
        Map<Integer, List<String>> leaderboard = game.getLeaderboard();
        Game.printLeaderboardToScreen(leaderboard);
    }
}