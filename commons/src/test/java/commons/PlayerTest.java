package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    Player player = new Player("player");

    @Test
    void testConstructor2() {
        Player p = new Player("player", "0");
        assertEquals(p.getName(),"player");
        assertEquals(p.getSocketID(),"0");
    }

    @Test
    void getGameID() {
        assertEquals(player.getGameID(),0);
    }

    @Test
    void setGameID() {
        player.setGameID(5);
        assertEquals(player.getGameID(),5);
    }

    @Test
    void getName() {
        assertEquals(player.getName(),"player");
    }

    @Test
    void getSocketID() {
        assertNull(player.getSocketID());
    }

    @Test
    void setSocketID() {
        player.setSocketID("ID1");
        assertEquals(player.getSocketID(),"ID1");
    }

    @Test
    void equalsTest()
    {
        Player a = new Player("player");
        Player b = new Player("player");
        Player c = new Player("p");
        assertEquals(a, a);
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertNotEquals(a, "str");
    }

    @Test
    void toStringTest()
    {
        Player p = new Player("player");
        assertEquals(p.toString(), "Player{id=0, name='player', socketID='null'}");
    }

}