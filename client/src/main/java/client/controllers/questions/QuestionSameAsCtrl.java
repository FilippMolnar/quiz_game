package client.controllers.questions;

import client.controllers.ControllerInitialize;
import client.controllers.MainAppController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.Activity;
import commons.Question;
import commons.UserReaction;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class QuestionSameAsCtrl extends AbstractQuestion implements ControllerInitialize {
    @FXML
    private Button optionA;
    @FXML
    private Button optionB;
    @FXML
    private Button optionC;
    @FXML
    private Label countA;
    @FXML
    private Label countB;
    @FXML
    private Label countC;
    @FXML
    private GridPane images;
    @FXML
    private Text activity;
    @FXML
    private ImageView answerImage;
    @FXML
    private Label cons;

    /**
     * Constructor for QuestionSameAsCtrl
     * @param server - the ServerUtils
     * @param mainCtrl - the MainAppController
     */
    @Inject
    public QuestionSameAsCtrl(ServerUtils server, MainAppController mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Initialize the scene
     */
    @Override
    public void initializeController() {
        scoreText.setText("SCORE " + mainCtrl.getScore());
        startTimerAnimation(10);
        resizeImages();
        hasSubmittedAnswer = false;
        optionA.setDisable(false);
        optionB.setDisable(false);
        optionC.setDisable(false);
        showJokerImages();
        resetUI();
        resetLogic();
    }

    /**
     * Initialize the scene
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
     * Getter for the first button
     * @return the first button
     */
    public Button getOptionA() {
        return optionA;
    }

    /**
     * Getter for the second button
     * @return the second button
     */
    public Button getOptionB() {
        return optionB;
    }

    /**
     * Getter for the third button
     * @return the third button
     */
    public Button getOptionC() {
        return optionC;
    }

    /**
     * Getter for the consumptions
     * @return the consumption
     */
    public Label getCons() {
        return cons;
    }

    /**
     * Sets the question
     * @param question - the question to set
     */
    public void setQuestion(Question question) {
        cons.setText("");
        cons_A.setText("");
        cons_B.setText("");
        cons_C.setText("");
        cons.setText("");
        setQuestionNumber(mainCtrl.getQuestionIndex());
        super.setQuestion(question);
        List<Node> imageViews = images.lookupAll(".image-view").stream().limit(4).toList();
        if ("Neither of these".equals(question.getChoices().get(0).getTitle())) {
            cons_A.setVisible(false);
        }
        else {
            cons_A.setVisible(true);
        }
        if ("Neither of these".equals(question.getChoices().get(1).getTitle())) {
            cons_B.setVisible(false);
        }
        else {
            cons_B.setVisible(true);
        }
        if ("Neither of these".equals(question.getChoices().get(2).getTitle())) {
            cons_C.setVisible(false);
        }
        else {
            cons_C.setVisible(true);
        }
        optionA.setText(question.getChoices().get(0).getTitle());
        optionB.setText(question.getChoices().get(1).getTitle());
        optionC.setText(question.getChoices().get(2).getTitle());
        activity.setText(question.getChoices().get(3).getTitle());

        if (question.getChoices().get(0).id == question.getCorrect().id) correctOption = 0;
        else if (question.getChoices().get(1).id == question.getCorrect().id) correctOption = 1;
        else correctOption = 2;
        System.out.println("Correct from same as is : "  + correctOption);

        for (int i = 0; i < 3; i++) {
            var view = (ImageView) imageViews.get(i);
            var choice = question.getChoices().get(i);
            Path path = Paths.get(choice.getImagePath());
            String groupID = path.getParent().getName(0).toString();
            try {
                var actualPath = getClass().getResource("/GoodActivities/" + groupID + "/" + path.getFileName()).toString();
                var newImage = new Image(actualPath);
                view.setFitWidth(1);
                view.setFitHeight(1);
                view.setImage(newImage);
            } catch (NullPointerException e) {
                System.out.println("Having an issue with the image " + path.getFileName() +
                        " it can't be found on the client");
                System.out.println("GROUP ID: " + groupID);
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        }
        Path path = Paths.get(question.getChoices().get(3).getImagePath());
        String groupID = path.getParent().getName(0).toString();
        var actualPath = getClass().getResource("/GoodActivities/" + groupID + "/" + path.getFileName()).toString();
        var newImage = new Image(actualPath);
        answerImage.setImage(newImage);
    }

    /**
     * Function called when user submits an answer
     * We mark that answer as final for now
     * @param actionEvent - event used to get the button
     */
    public void pressedOption(ActionEvent actionEvent) {
        final Node source = (Node) actionEvent.getSource();
        String button_id = source.getId();
        Activity a;
        if (button_id.equals("optionA")) {
            a = this.question.getChoices().get(0);
            selectedOption = 0;
        } else if (button_id.equals("optionB")) {
            a = this.question.getChoices().get(1);
            selectedOption = 1;
        } else {
            a = this.question.getChoices().get(2);
            selectedOption = 2;
        }
        optionA.setDisable(true);
        optionB.setDisable(true);
        optionC.setDisable(true);

        if(isMultiPlayer) {
            sendAnswerAndUpdateScore(mainCtrl, button_id, a);
        } else {
            sendAnswerAndUpdateScore(mainCtrl, button_id, a);
        }
    }


    /**
     * Resize the images
     */
    public void resizeImages() {
        List<Node> imageViews = images.lookupAll(".image-view").stream().limit(3).toList();
        for (Node imageView : imageViews) {
            var view = (ImageView) imageView;
            if(view.getParent() instanceof GridPane pane){
                if(pane.getId() != null && pane.getId().equals("answerImage")) {
                    view.setPreserveRatio(true);
                    view.setFitHeight(pane.getHeight() - 5);
                    view.setFitWidth(pane.getWidth() - 5);
                }
            }
            else if(view.getParent() instanceof StackPane pane) {
                view.setVisible(true); // fix not visible image
                view.setPreserveRatio(true);
                view.setFitHeight(pane.getHeight() - 5);
                view.setFitWidth(pane.getWidth() - 5);
            }
        }
    }

    /**
     * This method should be called whenever this scene is shown to reset the UI
     */
    private void resetUI() {
        informationLabel.setVisible(false);
        countA.setVisible(false);
        countB.setVisible(false);
        countC.setVisible(false);
        resetChart();
        optionA.setDisable(false);
        optionB.setDisable(false);
        optionC.setDisable(false);
    }

    /**
     * Reset the logic
     * Since there is only one instance of the controller, this needs to be done manually
     */
    private void resetLogic() {
        selectedOption = -1;
        this.hasSubmittedAnswer = false; // this is false at the beginning of the game
    }
}
