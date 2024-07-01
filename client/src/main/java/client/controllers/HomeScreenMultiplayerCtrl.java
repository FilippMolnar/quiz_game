package client.controllers;

import client.utils.ServerUtils;
import commons.Player;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.util.List;

public class HomeScreenMultiplayerCtrl {

    private final MainAppController appController;
    private final ServerUtils serverUtils;

    @FXML
    private TextField nameString;

    @FXML
    private Label labelErrors;

    @FXML
    private TextField serverField;

    /**
     * Constructor for the HomeScreenMultiplayerCtrl
     * @param serverUtils - the ServerUtils
     * @param appController - the MainAppController
     */
    @Inject
    HomeScreenMultiplayerCtrl(ServerUtils serverUtils, MainAppController appController) {
        this.appController = appController;
        this.serverUtils = serverUtils;
    }

    /**
     * Sets the name of a player
     * @param name - the name of a player
     */
    public void setName(String name) {
        nameString.setText(name);
    }

    /**
     * Method to enter the waiting room
     * @throws InterruptedException - is thrown when method is interrupted
     */
    public void enterRoom() throws InterruptedException {
        appController.initializeScore();
        appController.setGameMode(true);
        String name = nameString.getText();
        String finalName = name.substring(0, Math.min(name.length(), 16));

        if (name.isEmpty()) {
            labelErrors.setText("Please enter your name");
            return;
        }

        serverUtils.initializeServer(serverField.getText());
        // Get request for the players that are currently waiting
        List<Player> playersInWaitingRoom = serverUtils.getAllNamesInWaitingRoom();
        if (!playersInWaitingRoom.isEmpty()) {
            // Check if the player has a unique name
            for (Player player : playersInWaitingRoom) {
                if (player.getName().equals(finalName)) { // Another player with this name was found
                    labelErrors.setText("This name is already taken, please try another");
                    return;
                }
            }
        }
        labelErrors.setText(""); // reset the error label
        this.appController.showNext();
        this.appController.setName(finalName);
        this.serverUtils.sendThroughSocket("/app/enterRoom", new Player(finalName));
    }

    /**
     * Goes to homescreen when exit is clicked
     */
    public void backToHomeScreen() {
        appController.showHomeScreen();
    }

}
