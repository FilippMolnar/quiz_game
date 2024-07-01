package client.controllers.questions;

import client.controllers.ControllerInitialize;
import client.controllers.MainAppController;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class QuestionMultiOptionsCtrl extends AbstractQuestion implements ControllerInitialize {

    @FXML
    private GridPane images;

    /**
     * Constructor for QuestionMultiOptionsCtrl
     * @param server - the ServerUtils
     * @param mainCtrl - the MainAppController
     */
    @Inject
    public QuestionMultiOptionsCtrl(ServerUtils server, MainAppController mainCtrl) {
        super(server, mainCtrl);
    }

    /**
     * Initialize the scene
     */
    @Override
    public void initializeController() {
        this.scoreText.setText("SCORE " + mainCtrl.getScore());
        startTimerAnimation(10);
        resetUI();
        resetLogic();
        showJokerImages();
    }

    /**
     * Initialize the QuestionMultiOptionsCtrl
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
     * Sets the question
     * @param question - the question to set
     */
    public void setQuestion(Question question) {
        setQuestionNumber(mainCtrl.getQuestionIndex());
        super.setQuestion(question);
        List<Node> imageViews = images.lookupAll(".image-view").stream().limit(3).toList();
        optionA.setText(question.getChoices().get(0).getTitle());
        optionB.setText(question.getChoices().get(1).getTitle());
        optionC.setText(question.getChoices().get(2).getTitle());

        if (question.getChoices().get(0).id == question.getCorrect().id) correctOption = 0;
        else if (question.getChoices().get(1).id == question.getCorrect().id) correctOption = 1;
        else correctOption = 2;
        System.out.println("Correct from highest energy : "  + correctOption);


        for (int i = 0; i < imageViews.size(); i++) {
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
                view.setVisible(true);
            } catch (NullPointerException e) {
                System.out.println("Having an issue with the image " + path.getFileName() +
                        " it can't be found on the client");
                System.out.println("GROUP ID: " + groupID);
                System.out.println(Arrays.toString(e.getStackTrace()));

            }
        }
    }

    /**
     * Function called when user submits an answer
     * We mark that answer as final for now
     * @param actionEvent - event used to get the button
     */
    public void pressedOption(ActionEvent actionEvent) {
        final Button source = (Button) actionEvent.getSource();
        String button_id = source.getId();
        Activity a;
        if (button_id.equals("optionA")) {
            selectedOption = 0;
            a = question.getChoices().get(0);
        } else if (button_id.equals("optionB")) {
            selectedOption = 1;
            a = question.getChoices().get(1);
        } else {
            selectedOption = 2;
            a = question.getChoices().get(2);
        }
        if(a.id == question.getCorrect().id){
            System.out.println(button_id + "is Correct!");
        }else{
            System.out.println(button_id + "is Wrong!");
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
            StackPane pane = (StackPane) view.getParent();
            view.setPreserveRatio(true);
            view.setFitHeight(pane.getHeight() - 5);
            view.setFitWidth(pane.getWidth() - 5);
        }
    }

    /**
     * This method should be called whenever this scene is shown to reset the UI
     */
    private void resetUI() {
        selectedOption = -1;
        informationLabel.setVisible(false);
        countA.setVisible(false);
        countB.setVisible(false);
        countC.setVisible(false);
        resizeImages();
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
        this.hasSubmittedAnswer = false; // this is false at the beginning of the game
    }
}
