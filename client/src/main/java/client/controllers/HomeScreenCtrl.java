package client.controllers;

import client.utils.ServerUtils;

import javax.inject.Inject;

public class HomeScreenCtrl {

    private final MainAppController appController;
    private final ServerUtils serverUtils;

    /**
     * Constructor for HomeScreenCtrl
     * @param serverUtils - the ServerUtils
     * @param appController - the MainController
     */
    @Inject
    HomeScreenCtrl(ServerUtils serverUtils, MainAppController appController){
        this.appController = appController;
        this.serverUtils = serverUtils;
    }

    /**
     * Enter singleplayer homescreen
     * When singleplayer button is clicked
     */
    public void enterSinglePlayer() {
        appController.setGameMode(false);
        appController.setQuestionNumber(1);
        System.out.println("entering singleplayer screen");
        appController.showNext(1);
    }

    /**
     * Enter multiplayer homescreen
     * When multiplayer button is clicked
     */
    public void enterMultiPlayer() {
        appController.setGameMode(true);
        appController.setQuestionNumber(1);
        System.out.println("entering multiplayer screen");
        appController.showNext(0);
    }

    /**
     * Enter admin interface screen
     * When admin button is clicked
     */
    public void showAdminOverview() {
        System.out.println("entering admin screen");
        appController.showNext(2);
    }

    /**
     * Enter leaderboard screen
     * When leaderboard button is clicked
     */
    public void showLeaderBoard() {
        appController.showNext(3);
        LeaderBoardCtrl ctrl = (LeaderBoardCtrl) appController.getLinkedScene().getController();
        ctrl.getRematchButton().setOpacity(0);
        ctrl.getRematchButton().setDisable(true);
    }
}
