package client.jokers;

import client.controllers.MainAppController;
import client.controllers.questions.*;
import client.utils.ServerUtils;

public class DoublePointsJoker extends Joker{

    /**
     * Constructor for a DoublePointsJoker
     * @param name - name of the player
     * @param imagePath - image path
     * @param serverUtils - the ServerUtils
     */
    public DoublePointsJoker(String name, String imagePath, ServerUtils serverUtils) {
        super(name, imagePath, serverUtils);
    }

    /**
     * Method that is called when the joker is clicked
     * This uses the joker
     * @param mainCtrl - the MainAppController
     */
    public void onClick(MainAppController mainCtrl){
        if (isUsed()) {
            return;
        }
        playSound("double-points");
        System.out.println("DoublePointsJoker");
        AbstractQuestion.setDoublePointsJoker(true);

        use();
        markUsed(mainCtrl);
    }
}
