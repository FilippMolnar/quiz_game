package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserReactionTest {

    UserReaction ur;
    @BeforeEach
    void setUp() {
        ur = new UserReaction(123, "Angela", "angry");
    }

    @Test
    void getUsernameTest() {
        UserReaction u = new UserReaction();
        assertNull(u.getUsername());
        assertEquals("Angela", ur.getUsername());

    }

    @Test
    void setUsernameTest() {
        ur.setUsername("Paul");
        assertEquals("Paul", ur.getUsername());
    }

    @Test
    void setGameID() {
        ur.setGameID(56);
        assertEquals(56, ur.getGameID());
    }

    @Test
    void getReactionTest() {
        assertEquals("angry", ur.getReaction());
    }

    @Test
    void setReaction() {
        ur.setReaction("happy");
        assertEquals("happy", ur.getReaction());
    }

    @Test
    void testValidate() {
        assertThrows(IllegalArgumentException.class, () -> ur.setReaction("bored"));
        assertThrows(IllegalArgumentException.class, () -> ur.setReaction(null));
        assertDoesNotThrow(() -> ur.setReaction("angry"));
        assertDoesNotThrow(() -> ur.setReaction("angel"));
        assertDoesNotThrow(() -> ur.setReaction("happy"));
    }

    @Test
    void toStringTest() {
        assertEquals(ur.toString(), "Angela reacted with an angry emoji in game 123");
    }

}