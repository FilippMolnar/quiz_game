package client.controllers;

import client.LinkedScene;
import client.controllers.questions.AbstractQuestion;
import client.jokers.*;
import client.utils.ServerUtils;
import commons.Player;
import commons.Question;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WaitingRoomCtrl implements Initializable, ControllerInitialize {

    private final MainAppController appController;
    private final ServerUtils serverUtils;
    private List<Player> playerList = new ArrayList<Player>();

    @FXML
    private GridPane pane;

    @FXML
    private Label morePlayersWaitingRoomLabel;

    /**
     * Constructor for WaitingRoomCtrl
     * @param appController - the MainAppController
     * @param serverUtils - the ServerUtils
     */
    @Inject
    WaitingRoomCtrl(MainAppController appController, ServerUtils serverUtils) {
        this.appController = appController;
        this.serverUtils = serverUtils;
    }

    /**
     * Initialize the WaitingRoomCtrl
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.serverUtils.subscribeForSocketMessages("/topic/waitingRoom", Player.class, player -> {
            playerList.add(player);
            movePlayers(player);
        });
        this.serverUtils.subscribeForSocketMessages("/topic/disconnect", Player.class, player -> {
            System.out.println("Player " + player.getName() + " disconnected");
            playerList.remove(player);
            updateUI();
        });
        this.serverUtils.subscribeForSocketMessages("/user/queue/startGame/gameID", Integer.class, gameID -> {
            appController.setGameID(gameID);
            List<Question> questions = serverUtils.getAllGameQuestions(gameID);
            System.out.println("appController.isMultiPlayer()");
            System.out.println(appController.isMultiPlayer());
            appController.setJokers(new JokersList(serverUtils, appController.isMultiPlayer()));
            appController.addQuestionScenes(questions, 0);
            appController.showNext();
        });
        this.serverUtils.subscribeForSocketMessages("/user/queue/decrease_time/gameID", Integer.class, gameID -> {
            System.out.println("decreased");
            LinkedScene current = appController.getCurrentScene();
            if(current.getController() instanceof AbstractQuestion qCtrl){
                DecreaseTimeJoker.decreaseTime(qCtrl);
            }
        });
        this.serverUtils.subscribeForSocketMessages("/user/queue/increase_time/gameID", Integer.class, gameID -> {
            System.out.println("increased");
            LinkedScene current = appController.getCurrentScene();
            if(current.getController() instanceof AbstractQuestion qCtrl){
                GoogleJoker.increaseTime(qCtrl);
            }
        });
        this.serverUtils.subscribeForSocketMessages("/user/queue/cover_ink/gameID", Integer.class, gameID -> {
            System.out.println("cover_ink");
            LinkedScene current = appController.getCurrentScene();
            if(current.getController() instanceof AbstractQuestion qCtrl){
                CoverInkJoker.splashAnimation(qCtrl);
            }
        });
        this.serverUtils.subscribeForSocketMessages("/user/queue/cover_hands/gameID", Integer.class, gameID -> {
            System.out.println("cover_hands");
            LinkedScene current = appController.getCurrentScene();
            if(current.getController() instanceof AbstractQuestion qCtrl){
                CoverHandsJoker.handsAnimation(qCtrl);
            }
        });
        this.serverUtils.subscribeForSocketMessages("/user/queue/barrel_roll/gameID", Integer.class, gameID -> {
            System.out.println("barrel_roll");
            LinkedScene current = appController.getCurrentScene();
            if(current.getController() instanceof AbstractQuestion qCtrl){
                BarrelRollJoker.barrelRoll(qCtrl);
            }
        });

    }

    /**
     * Initialize the scene
     */
    @Override
    public void initializeController() {
        updateUI();
    }

    /**
     * Moves all players one to the right and adds one player up front.
     * @param toAdd the player to add at the front
     */
    public void movePlayers(Player toAdd) {
        String name = toAdd.getName();
        int numOfPlaces = pane.getChildren().size();
        var places = pane.getChildren();
        for (int i = 0; i < Math.min(numOfPlaces, playerList.size()); i++) {
            StackPane place = (StackPane) places.get(i);
            Label label = (Label) place.getChildren().get(1);
            String nextName = label.getText();
            label.setText(name);
            place.setVisible(true);
            name = nextName;
        }
        if (playerList.size() > numOfPlaces) {
            morePlayersWaitingRoomLabel.setVisible(true);
            morePlayersWaitingRoomLabel.setText("and " + (playerList.size() - numOfPlaces) + " more players");
        }
    }

    /**
     * Update the UI based on the <code>playerList</code>
     */
    public void updateUI() {
        for (Node node : pane.getChildren()) {
            node.setVisible(false); // there are 6-7 circle added by default but I hide them
        }
        morePlayersWaitingRoomLabel.setVisible(false); // hide the label
        this.playerList = serverUtils.getAllNamesInWaitingRoom(); // get request on the players that are currently waiting
        System.out.println("The player list is " + this.playerList);
        for (Player player : this.playerList) {
            movePlayers(player);
        }
    }

    /**
     * Method to go back to the homescreen
     * Linked to exit button
     */
    public void goBack() {
        serverUtils.sendThroughSocket("/app/disconnect", new Player(this.appController.getName()));
        this.appController.showHomeScreen();
    }

    /**
     * Start the game
     */
    public void startGame() {
        serverUtils.sendThroughSocket("/app/startGame", new Player(this.appController.getName()));
        serverUtils.postStartGame();
    }
}
