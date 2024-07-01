
package client.controllers;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminOverviewCtrl implements Initializable, ControllerInitialize {

    private final MainAppController appController;
    private final ServerUtils serverUtils;
    private AdminEditCtrl editCtrl;
    private ObservableList<Activity> data;

    @FXML
    private TableView<Activity> activityTable;
    @FXML
    private TableColumn<Activity, String> titleColumn;
    @FXML
    private TableColumn<Activity, String> sourceColumn;
    @FXML
    private TableColumn<Activity, Integer> consumptionColumn;
    @FXML
    private TableColumn<Activity, String> imageColumn;

    @FXML
    Button exitButton;
    @FXML
    Button addButton;
    @FXML
    Button editButton;
    @FXML
    Button deleteButton;
    @FXML
    Label error;

    /**
     * Constructor for AdminEditCtrl
     * @param serverUtils - the ServerUtils
     * @param appController - the MainAppController
     */
    @Inject
    AdminOverviewCtrl(ServerUtils serverUtils, MainAppController appController){
        this.appController = appController;
        this.serverUtils = serverUtils;
        this.editCtrl = new AdminEditCtrl(serverUtils, appController);
    }

    /**
     * Initialize the AdminOverviewCtrl
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        titleColumn.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().getTitle()));
        sourceColumn.setCellValueFactory(x -> {
            String source = x.getValue().getSource();
            if(source != null) {
                return new SimpleStringProperty(source);
            } else {
                return new SimpleStringProperty("");
            }
        });
        consumptionColumn.setCellValueFactory(x -> (new SimpleIntegerProperty(x.getValue().getConsumption())).asObject());
        imageColumn.setCellValueFactory(x -> new SimpleStringProperty(x.getValue().getImagePath()));
        refresh();
    }

    /**
     * Initialize the scene
     */
    public void initializeController() {
        refresh();
    }

    /**
     * Refreshes the data in the TableView.
     */
    public void refresh() {
        serverUtils.initializeServer("localhost");
        var activities = serverUtils.getAllActivities();
        data = FXCollections.observableList(activities);
        this.activityTable.setItems(data);
    }

    /**
     * Set the AdminEditCtrl
     * @param editCtrl - the AdminEditCtrl
     */
    public void setEditCtrl(AdminEditCtrl editCtrl) {
        this.editCtrl = editCtrl;
    }

    /**
     * Retrieves the activity the user selects from table
     * @return Activity that user selected
     */
    public Activity retrieveActivity(){
        if (activityTable != null) {
            return activityTable.getSelectionModel().getSelectedItem();
        }
        else return null;
    }

    /**
     * Goes to homescreen when exitButton is clicked
     */
    public void exit() {
        appController.showHomeScreen();
    }

    /**
     * Goes to edit screen to add an activity when the addButton is clicked
     */
    public void toAddActivity() {
        Activity a = new Activity("",0, "","");
        //appController.showAdminEdit(a);
        appController.showNext();
        refresh();
    }

    /**
     * Goes to edit screen to edit an activity when the editButton is clicked
     */
    public void toEditActivity() {
        Activity selectedActivity = retrieveActivity();
        if (selectedActivity == null) {
            error.setVisible(true);
        }
        else {
            error.setVisible(false);
            activityTable.getItems().remove(selectedActivity);
            serverUtils.deleteActivity(selectedActivity);
            appController.showNext();
            editCtrl.showEditActivity(selectedActivity);
        }
        refresh();
    }

    /**
     * Deletes the selected activity from the repo and table when deleteButton is called
     */
    public void deleteActivity() {
        Activity a = retrieveActivity();
        serverUtils.deleteActivity(a);
        activityTable.getItems().remove(a);
        refresh();
    }

}
