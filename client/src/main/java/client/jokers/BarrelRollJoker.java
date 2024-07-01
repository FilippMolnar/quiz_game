package client.jokers;

import client.controllers.MainAppController;
import client.controllers.questions.AbstractQuestion;
import client.utils.ServerUtils;
import commons.Player;
import javafx.animation.RotateTransition;
import javafx.scene.layout.GridPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;


public class BarrelRollJoker extends Joker{

    /**
     * Constructor for a BarrelRollJoker
     * @param name - name of the player
     * @param imagePath - image path
     * @param serverUtils - the ServerUtils
     */
    public BarrelRollJoker(String name, String imagePath, ServerUtils serverUtils) {
        super(name, imagePath, serverUtils);
    }

    /**
     * Method that is called when the joker is clicked
     * This uses the joker
     * @param mainCtrl - the MainAppController
     */
    public void onClick(MainAppController mainCtrl){
        if(isUsed()){
            return;
        }
        Player p = new Player(mainCtrl.getName());
        p.setGameID(mainCtrl.getGameID());
        serverUtils.sendThroughSocket("/app/barrel_roll", p);

        System.out.println("Barrel Roll Joker");
        use();
        markUsed(mainCtrl);
    }

    /**
     * Plays the animation for the barrel roll
     * @param qCtrl - the AbstractQuestion controller
     */
    public static void barrelRoll(AbstractQuestion qCtrl) {
        System.out.println("Animating the barrel roll");
        GridPane gridPane = qCtrl.parentGridPane;
        RotateTransition rotateTransition = new RotateTransition();
        rotateTransition.setDuration(Duration.millis(1500));
        rotateTransition.setByAngle(360);
        rotateTransition.setAxis(Rotate.Z_AXIS);
        rotateTransition.setNode(gridPane);
        rotateTransition.setCycleCount(3);
        rotateTransition.setAutoReverse(true);
        rotateTransition.play();
    }
}

