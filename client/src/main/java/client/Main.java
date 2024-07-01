/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import client.controllers.*;
import client.controllers.questions.QuestionInsertNumberCtrl;
import client.controllers.questions.QuestionMultiOptionsCtrl;
import client.controllers.questions.QuestionSameAsCtrl;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule()); // creates Guice injector based on the module
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * This way of loading the stages makes sure the <code>Juice</code> injects the right dependencies to the controllers
     *
     * @param primaryStage - the window that is always visible
     */
    private void loadUsingTemplateDependencyInjection(Stage primaryStage) {
        var waitingRoom = FXML.load(WaitingRoomCtrl.class,
                "client", "scenes", "waiting_room.fxml");
        var home = FXML.load(HomeScreenCtrl.class,
                "client", "scenes", "HomeScreen.fxml");
        var homeSingleplayer = FXML.load(HomeScreenSingleplayerCtrl.class,
                "client", "scenes", "HomeScreenSingleplayer.fxml");
        var homeMultiplayer = FXML.load(HomeScreenMultiplayerCtrl.class,
                "client", "scenes", "HomeScreenMultiplayer.fxml");
        var leaderBoard = FXML.load(LeaderBoardCtrl.class,
                "client", "scenes", "Leaderboard.fxml");
        var qMulti = FXML.load(QuestionMultiOptionsCtrl.class,
                "client", "scenes", "QuestionMultiOptions.fxml");
        var qInsert = FXML.load(QuestionInsertNumberCtrl.class,
                "client", "scenes", "QuestionInsertNumber.fxml");
        var qTransition = FXML.load(TransitionSceneCtrl.class,
                "client", "scenes", "transition_between_questions.fxml");
        var sameAs = FXML.load(QuestionSameAsCtrl.class,
                "client", "scenes", "QuestionSameAs.fxml");
        var adminEdit = FXML.load(AdminEditCtrl.class,
                "client","scenes","AdminEdit.fxml");
        var adminOverview = FXML.load(AdminOverviewCtrl.class,
                "client","scenes","AdminOverview.fxml");
        MainAppController appcontroller = INJECTOR.getInstance(MainAppController.class);
        appcontroller.initialize(primaryStage, waitingRoom, home, homeSingleplayer, homeMultiplayer, leaderBoard, qMulti, qInsert, sameAs, qTransition,adminOverview,adminEdit);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        loadUsingTemplateDependencyInjection(primaryStage);
    }
}
