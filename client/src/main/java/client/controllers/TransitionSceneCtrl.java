package client.controllers;

import client.controllers.questions.AbstractQuestion;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class TransitionSceneCtrl extends AbstractQuestion implements ControllerInitialize {

    @FXML
    Label questionText;

    @FXML
    Line loadingLine;

    /**
     * Constructor for TransitionScene
     * @param server - the ServerUtils
     * @param mainCtrl - the MainAppController
     */
    @Inject
    public TransitionSceneCtrl(ServerUtils server, MainAppController mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Initialize the controller
     * Show the next scene after a delay of animationDuration seconds
     */
    @Override
    public void initializeController() {
        double animationDuration = 1;
        loadingLine.setEndX(-40); // for animation hardcoded
        questionText.setText("Question " + (mainCtrl.getQuestionIndex()));
        Timeline timeline = new Timeline();
        KeyValue lineLength = new KeyValue(loadingLine.endXProperty(), 128); // some hardcoded value for animation
        EventHandler<ActionEvent> onFinished = t -> {
            Platform.runLater(() -> {
                System.out.println("Calling next from question transition!");
                mainCtrl.showNext();
            }); // show next scene after animation
        };
        KeyFrame keyFrame1 = new KeyFrame(Duration.millis(animationDuration * 1000),
                onFinished, lineLength);
        timeline.getKeyFrames().add(keyFrame1);
        timeline.play();
    }

}
