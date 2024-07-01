package server;

import commons.Player;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.Set;

public class Utils {

    private final SimpMessageSendingOperations simpMessagingTemplate;

    public Utils(SimpMessageSendingOperations simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Send data over socket to all players
     * @param playerList The list of players which should receive data
     * @param destination Socket destination
     * @param payload the object which to send
     */
    public void sendToAllPlayers(Set<Player> playerList, String destination, Object payload){
        for (Player player : playerList) {
            String playerID = player.getSocketID();
            simpMessagingTemplate.convertAndSendToUser(playerID, destination, payload);
        }
    }
}
