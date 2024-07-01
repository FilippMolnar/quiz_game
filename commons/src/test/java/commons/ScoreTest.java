package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScoreTest {

    @Test
    void testConstructor() {
        Score s = new Score("s", 5);
        assertEquals(s.getName(), "s");
        assertEquals(s.getScore(), 5);
    }

    @Test
    void testSetScore() {
        Score s = new Score("s", 5);
        assertEquals(s.getScore(), 5);
        s.setScore(10);
        assertEquals(s.getScore(), 10);
    }

    @Test
    void testAddScore() {
        Score s = new Score("s", 5);
        assertEquals(s.getScore(), 5);
        s.addScore(10);
        assertEquals(s.getScore(), 15);
    }

    @Test
    void testEquals() {
        Score a = new Score("s", 5);
        Score a_same = new Score("s", 5);
        Score b = new Score("s", 6);
        Score c = new Score("b", 5);
        assertTrue(a.equals(a));
        assertTrue(a.equals(a_same));
        assertFalse(a.equals("str"));
        assertFalse(a.equals(b));
        assertFalse(a.equals(c));
    }

    @Test
    void testToString() {
        Score s = new Score("s", 5);
        assertEquals(s.toString(), "s: 5");
    }
}