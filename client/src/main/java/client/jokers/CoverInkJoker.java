package client.jokers;

import client.controllers.MainAppController;
import client.controllers.questions.AbstractQuestion;
import client.utils.ServerUtils;
import commons.Player;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

public class CoverInkJoker extends Joker{

    /**
     * Constructor for a CoverInkJoker
     * @param name - name of the player
     * @param imagePath - image path
     * @param serverUtils - the ServerUtils
     */
    public CoverInkJoker(String name, String imagePath, ServerUtils serverUtils) {
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
        serverUtils.sendThroughSocket("/app/cover_ink", p);

        System.out.println("Cover Screen with Ink Joker");
        use();
        markUsed(mainCtrl);
    }

    /**
     * Plays the animation for the ink
     * @param qCtrl - the AbstractQuestion controller
     */
    public static void splashAnimation(AbstractQuestion qCtrl) {
        playSound("ink");
        int noOfSplatters = 3+ (int) Math.round(Math.random()*3);
        for (int i = 0; i < noOfSplatters; i++) {
            int duration = qCtrl.getTimerIntegerValue() * 1000;

            Pane pane = new Pane();

            /* create splash */
            int splashType = (int)Math.floor((Math.random()*2))+1;
            String path = "/client/pictures/splash"+splashType+".png";
            Image img = new Image(CoverInkJoker.class.getResource(path).toString());
            ImageView iv = new ImageView(img);
            pane.getChildren().add(iv);
            iv.setX(100.0+Math.random()*800.0);
            iv.setY(100.0+Math.random()*1000.0);
            iv.setPreserveRatio(true);
            iv.setFitHeight(100+Math.random()*500);

            /* add transitions */
            TranslateTransition translate = new TranslateTransition();
            translate.setByY(50);
            translate.setDuration(Duration.millis(7000));
            translate.setNode(pane);
            translate.play();

            FadeTransition fade = new FadeTransition();
            fade.setDuration(Duration.millis(Math.min(7000, duration)));
            fade.setFromValue(10);
            fade.setToValue(0);
            fade.setNode(pane);
            fade.setDelay(Duration.millis(4000));
            fade.play();

            pane.setManaged(false);

            /* add in random place */
            int row = (int) Math.floor(qCtrl.parentGridPane.getRowCount()*Math.random());
            int column = (int) Math.floor(qCtrl.parentGridPane.getRowCount()*Math.random());
            qCtrl.parentGridPane.add(pane, row, column);

            /* delete after 7 seconds or after scene change */
            Timer timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater( () -> {
                        pane.setDisable(true);
                        qCtrl.parentGridPane.getChildren().remove(pane);
                    });
                }
            };
            timer.schedule(timerTask, Math.min(7000, duration));
        }
    }
}
