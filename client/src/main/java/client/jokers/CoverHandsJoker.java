package client.jokers;

import client.controllers.MainAppController;
import client.controllers.questions.AbstractQuestion;
import client.utils.ServerUtils;
import commons.Player;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

public class CoverHandsJoker extends Joker{

    /**
     * Constructor for a CoverHandsJoker
     * @param name - name of the player
     * @param imagePath - image path
     * @param serverUtils - the ServerUtils
     */
    public CoverHandsJoker(String name, String imagePath, ServerUtils serverUtils) {
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
        serverUtils.sendThroughSocket("/app/cover_hands", p);

        System.out.println("Cover Screen with Hands Joker");
        use();
        markUsed(mainCtrl);
    }

    /**
     * Plays the animation for the hands
     * @param qCtrl - the AbstractQuestion controller
     */
    public static void handsAnimation(AbstractQuestion qCtrl){
        playSound("hands");
        int duration = qCtrl.getTimerIntegerValue() * 1000;

        /* create hands */
        String pathLeft = "/client/pictures/left_hand.png";
        String pathRight = "/client/pictures/right_hand.png";
        Image imgLeft = new Image(CoverHandsJoker.class.getResource(pathLeft).toString());
        Image imgRight = new Image(CoverHandsJoker.class.getResource(pathRight).toString());
        ImageView ivLeft = new ImageView(imgLeft);
        ImageView ivRight = new ImageView(imgRight);
        ivRight.setPreserveRatio(true);
        ivLeft.setPreserveRatio(true);
        ivLeft.setFitWidth(700);
        ivRight.setFitWidth(700);

        /* add transition LEFT */
        TranslateTransition translateLeft = new TranslateTransition();
        translateLeft.setFromX(200);
        translateLeft.setByX(-400);
        translateLeft.setDuration(Duration.millis(7000));
        translateLeft.setNode(ivLeft);
        translateLeft.play();

        /* add transition Right */
        TranslateTransition translateRight = new TranslateTransition();
        translateRight.setFromX(-400);
        translateRight.setByX(200);
        translateRight.setDuration(Duration.millis(7000));
        translateRight.setNode(ivRight);
        translateRight.play();

        /* add in place */
        qCtrl.parentGridPane.add(ivLeft, qCtrl.parentGridPane.getColumnCount() - 1, 1);
        qCtrl.parentGridPane.add(ivRight, 0, 1);

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater( () -> {
                    ivLeft.setDisable(true);
                    ivRight.setDisable(true);
                    qCtrl.parentGridPane.getChildren().remove(ivLeft);
                    qCtrl.parentGridPane.getChildren().remove(ivRight);
                });
            }
        };
        timer.schedule(timerTask, Math.min(7000, duration));
    }

}
