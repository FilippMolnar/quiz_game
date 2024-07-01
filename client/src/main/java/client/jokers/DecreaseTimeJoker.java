package client.jokers;

import client.controllers.MainAppController;
import client.controllers.questions.AbstractQuestion;
import client.utils.ServerUtils;
import commons.Player;

public class DecreaseTimeJoker extends Joker{

    /**
     * Constructor for a DecreaseTimeJoker
     * @param name - name of the player
     * @param imagePath - image path
     * @param serverUtils - the ServerUtils
     */
    public DecreaseTimeJoker(String name, String imagePath, ServerUtils serverUtils) {
        super(name, imagePath, serverUtils);
    }

    /**
     * Method that is called when the joker is clicked
     * This uses the joker
     * @param mainCtrl - the MainAppController
     */
    public void onClick(MainAppController mainCtrl){
        if (isUsed()){
            return;
        }

        System.out.println("DecreaseTimeJoker");
        Player p = new Player(mainCtrl.getName());
        p.setGameID(mainCtrl.getGameID());
        serverUtils.sendThroughSocket("/app/decrease_time", p);

        use();
        markUsed(mainCtrl);
    }

    /**
     * Decreases the time for other players
     * @param qCtrl - the AbstractQuestion controller
     */
    public static void decreaseTime(AbstractQuestion qCtrl){
        playSound("reduce-time");
        qCtrl.cutAnimationInHalf();
    }
}
