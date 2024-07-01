package client.jokers;

import client.controllers.MainAppController;
import client.controllers.questions.AbstractQuestion;
import client.utils.ServerUtils;
import commons.Player;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


public class GoogleJoker extends Joker{

    /**
     * Constructor for a GoogleJoker
     * @param name - name of the player
     * @param imagePath - image path
     * @param serverUtils - the ServerUtils
     */
    public GoogleJoker(String name, String imagePath, ServerUtils serverUtils) {
        super(name, imagePath, serverUtils);
    }

    /**
     * Method that is called when the joker is clicked
     * This uses the joker
     * @param mainCtrl - the MainAppController
     */
    public void onClick(MainAppController mainCtrl)
    {
        if(isUsed())return ;
        playSound("google");
        Player p = new Player(mainCtrl.getName());
        p.setGameID(mainCtrl.getGameID());
        serverUtils.sendThroughSocket("/app/increase_time", p);

        Desktop desktop = Desktop.getDesktop();
        try{
            URI url = new URI("https://www.google.com");
            desktop.browse(url);
        }catch(URISyntaxException | IOException e){
            e.printStackTrace();
        }

        use();
        markUsed(mainCtrl);
    }

    /**
     * Increases the time for googling (just for the player that uses it)
     * @param qCtrl - the AbstractQuestion controller
     */
    public static void increaseTime(AbstractQuestion qCtrl)
    {
        qCtrl.addTimeForGoogling();
    }
}
