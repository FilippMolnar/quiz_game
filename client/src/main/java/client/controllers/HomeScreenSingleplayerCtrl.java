package client.controllers;

import client.utils.ServerUtils;
import commons.Question;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.util.ArrayList;

public class HomeScreenSingleplayerCtrl {

    private final MainAppController appController;
    private final ServerUtils serverUtils;

    @FXML
    private TextField nameString;

    /**
     * Constructor for the HomeScreenSingleplayerCtrl
     * @param serverUtils - the ServerUtils
     * @param appController - the MainAppController
     */
    @Inject
    HomeScreenSingleplayerCtrl(ServerUtils serverUtils, MainAppController appController){
        this.appController = appController;
        this.serverUtils = serverUtils;
    }

    /**
     * Enter the game
     */
    public void enterRoom(){
        String name = nameString.getText();
        System.out.println(name);
        String finalName = name.substring(0,Math.min(name.length(),16));
        serverUtils.initializeServer("localhost");
        ArrayList<Question> questions = serverUtils.getRandomQuestions();
        appController.addQuestionScenes(questions, 1);
        this.appController.setName(finalName);
        this.appController.initializeScore();
        // Show single player. 0 would be multiplayer.
        this.appController.showNext();
        this.appController.setGameMode(false);
    }

    /**
     * Goes to homescreen when exit is clicked
     */
    public void backToHomeScreen() {
        appController.showHomeScreen();
    }
}
