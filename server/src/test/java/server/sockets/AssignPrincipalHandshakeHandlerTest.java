package server.sockets;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.when;

class AssignPrincipalHandshakeHandlerTest {

    AssignPrincipalHandshakeHandler handShake = Mockito.mock(AssignPrincipalHandshakeHandler.class,CALLS_REAL_METHODS );
    @Test
    void determineUser() {
        Map<String,Object> map = new HashMap<>();
        when(handShake.generateRandomUsername()).thenReturn("random-string");
        var principal = handShake.determineUser(null, null, map);
        assertEquals(principal.getName(),"random-string");
    }
    @Test
    void generateRandomUsername() {
        assertNotNull(handShake.generateRandomUsername());
    }
}