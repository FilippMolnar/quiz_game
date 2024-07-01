package server;

import commons.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.verify;

class UtilsTest {


//    public void sendToAllPlayers(Set<Player> playerList, String destination, Object payload){
//        for (Player player : playerList) {
//            String playerID = player.getSocketID();
//            simpMessagingTemplate.convertAndSendToUser(playerID, destination, payload);
//        }
//    }



    @Test
    void sendToAllPlayers() {
        SimpMessagingTemplate simpMessagingTemplate = Mockito.mock(SimpMessagingTemplate.class);
        Utils utils = new Utils(simpMessagingTemplate);
        List<Player> players = new ArrayList<Player>();
        players.add(new Player("1","id1"));
        players.add(new Player("2","id2"));
        players.add(new Player("3","id3"));
        Set<Player> playerSet = new HashSet<>(players);

        utils.sendToAllPlayers(playerSet, "/topic/somewhere",10);
        for(Player player : playerSet){
            verify(simpMessagingTemplate).convertAndSendToUser(
                    player.getSocketID(),"/topic/somewhere",10);
        }

    }
}