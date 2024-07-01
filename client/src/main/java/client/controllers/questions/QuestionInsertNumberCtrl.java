package client.controllers.questions;

import client.controllers.ControllerInitialize;
import client.controllers.MainAppController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Answer;
import commons.Question;
import commons.UserReaction;
import io.github.palexdev.materialfx.controls.MFXSlider;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.control.Button;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class QuestionInsertNumberCtrl extends AbstractQuestion implements ControllerInitialize {

    @FXML
    private Button submitButton;
    @FXML
    private Text scoreText;
    @FXML
    private MFXSlider slider;
    @FXML
    private GridPane images;
    @FXML
    private Text activity;
    @FXML
    private ImageView image;
    @FXML
    protected GridPane parentGridPane;

    /**
     * Constructor for QuestionInsertNumberCtrl
     * @param server - the ServerUtils
     * @param mainCtrl - the MainAppController
     */
    @Inject
    public QuestionInsertNumberCtrl(ServerUtils server, MainAppController mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Initialize the QuestionInsertNumberCtrl
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        server.subscribeForSocketMessages("/user/queue/reactions", UserReaction.class, userReaction -> {
            System.out.println("received reaction!");
            userReaction(userReaction.getReaction(), userReaction.getUsername());
        });
        server.subscribeForSocketMessages("/user/queue/statistics", List.class, this::displayAnswers);
    }

    /**
     * Initialize the scene
     */
    @Override
    public void initializeController() {
        slider.setMouseTransparent(false);
        slider.setDisable(false);
        this.informationLabel.setVisible(false);
        this.scoreText.setText("SCORE " + mainCtrl.getScore());
        startTimerAnimation(10);
        resizeImages();
        resetLogic();
        submitButton.setDisable(false);
        int correct = mainCtrl.getCorrect().getConsumption();
        int min = (int) (Math.random()*correct);
        slider.setMin(min);
        int max = (int) ((Math.random()+1)*correct);
        System.out.println("Min is : " + min + " : Max is : " + max);
        slider.setMax(max);
        int middle = (min+max)/2;
        slider.setValue(middle);
        showJokerImages();
    }

    /**
     * Displays the consumptions and correct answer after everyone has answered
     * @param answer - the list of consumptions
     */
    @Override
    public void displayAnswers(List<Integer> answer) {
        scoreText.setText("SCORE " + mainCtrl.getScore());
        // guard the socket message call with this check
        if(!(mainCtrl.getCurrentScene().getController().getClass() == getClass())) {
            return;
        }
        System.out.println("Calling display answers from QInsert");
        TimerTask delay = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(mainCtrl::showNext);
            }
        };
        Timer myTimer = new Timer();
        int consumption = mainCtrl.getCorrect().getConsumption();
        slider.setValue(consumption);
        slider.animateOnPressProperty();
        myTimer.schedule(delay, 3000); // wait for 4 seconds
    }

    /**
     * Submits the answer
     */
    public void submitAnswer() {
        stopTimer();
        int answer = (int) slider.getValue();
        slider.setMouseTransparent(true);
        slider.setDisable(true);
        submitButton.setDisable(true);
        int newScore = calculateScore(Double.parseDouble(timerValue.getText()));
        mainCtrl.updateScore(newScore);
        if(isMultiPlayer) {
            sendAnswer(new Answer(true, answer+"", mainCtrl.getGameID(), newScore, mainCtrl.getName()));
        } else {
            displayAnswers(new ArrayList());
        }
    }

    /**
     * Sets the question
     * @param question - the question to set
     */
    public void setQuestion(Question question) {
        setQuestionNumber(mainCtrl.getQuestionIndex());
        super.setQuestion(question);
        var choice = question.getCorrect();
        activity.setText(choice.getTitle());
        Path path = Paths.get(choice.getImagePath());
        System.out.println(path.toString());
        String groupID = path.getParent().getName(0).toString();
        try {
            var actualPath = getClass().getResource("/GoodActivities/" + groupID + "/" + path.getFileName()).toString();
            var newImage = new Image(actualPath);
            image.setImage(newImage);
            System.out.println(path.getFileName() + " " + actualPath);
        } catch (NullPointerException e) {
            System.out.println("Having an issue with the image " + path.getFileName() +
                    " it can't be found on the client");
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        image3.setOpacity(0.0);
        circle3.setOpacity(0.0);
    }


    /**
     * Resize the images
     */
    public void resizeImages() {
        List<Node> imageViews = images.lookupAll(".image-view").stream().limit(4).toList();
        for (Node imageView : imageViews) {
            var view = (ImageView) imageView;
            StackPane pane = (StackPane) view.getParent();
            view.setPreserveRatio(true);
            view.setFitHeight(pane.getHeight() - 5);
            view.setFitWidth(pane.getWidth() - 5);
        }
    }

    /**
     * Reset the logic
     * Since there is only one instance of the controller, this needs to be done manually
     */
    private void resetLogic() {
        this.hasSubmittedAnswer = false; // this is false at the beginning of the game
    }

    /**
     * Method to calculate score for the estimate question
     * @param secondsToAnswer - the seconds it took the player to answer the question
     * @return the score to be added to the player's score
     */
    public int calculateScore(double secondsToAnswer) {
        int answerPlayer = (int) slider.getValue();
        int correctAnswer = question.getCorrect().getConsumption();

        int smallRangeLeft = (int) (correctAnswer * 0.9);
        int smallRangeRight = (int) (correctAnswer * 1.1);
        int mediumRangeLeft = (int) (correctAnswer * 0.7);
        int mediumRangeRight = (int) (correctAnswer * 1.3);
        int bigRangeLeft = (int) (correctAnswer * 0.5);
        int bigRangeRight = (int) (correctAnswer * 1.5);

        int rangeBonus = 0;
        int maxPointsRange = 50;
        if (smallRangeLeft <= answerPlayer && answerPlayer <= smallRangeRight) {
            rangeBonus = 40;
        }
        else if (mediumRangeLeft <= answerPlayer && answerPlayer <= mediumRangeRight) {
            rangeBonus = 25;
        }
        else if (bigRangeLeft <= answerPlayer && answerPlayer <= bigRangeRight) {
            rangeBonus = 15;
        }

        double maxSeconds = 10;
        int maxPointsTime = 50;
        int timeBonus = (int) Math.round(maxPointsTime * (1 - ((secondsToAnswer / maxSeconds) / 2)));

        int scoreToBeAdded = 0;
        if (answerPlayer == correctAnswer) {
            scoreToBeAdded = maxPointsRange + timeBonus;
        }
        else {
            scoreToBeAdded = timeBonus + rangeBonus;
        }
        return scoreToBeAdded;
    }
}
