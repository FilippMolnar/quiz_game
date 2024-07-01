
package client.controllers;

import client.utils.ServerUtils;
import commons.Activity;
import javafx.fxml.FXML;

import javax.inject.Inject;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

import static java.lang.Integer.parseInt;

public class AdminEditCtrl implements ControllerInitialize {

    private final MainAppController appController;
    private final ServerUtils serverUtils;

    @FXML
    private TextField activityTitleField;
    @FXML
    private TextField activitySourceField;
    @FXML
    private TextField activityConsumptionField;
    @FXML
    private TextField activityImageField;

    Activity selectedActivity;

    File sourceFile;
    File destinationFile;

    /**
     * Constructor for AdminEditCtrl
     * @param serverUtils - the ServerUtils
     * @param appController - the MainAppController
     */
    @Inject
    AdminEditCtrl(ServerUtils serverUtils, MainAppController appController){
        this.appController = appController;
        this.serverUtils = serverUtils;
    }

    /**
     * Gets the activity currently on the edit screen
     * @return The activity currently on the edit screen
     */
    public Activity getActivity() {
        String title = activityTitleField.getText();
        String source = activitySourceField.getText();
        String image = activityImageField.getText();
        int consumption = parseInt(activityConsumptionField.getText());
        return new Activity(title, consumption, image, source);
    }

    /**
     * Goes to homescreen when exitButton is clicked
     */
    public void exit() {
        appController.showHomeScreen();
    }

    /**
     * Adds activity to ActivityRepository
     * The activity that is added is the one currently on the edit screen
     */
    public void addActivity() {
        Activity a = getActivity();
        serverUtils.addAct(a);
    }

    /**
     * Shows the activity the user selected to edit
     * @param activity - The activity the user selected to edit
     */
    public void showEditActivity(Activity activity) {
        // Show original activity
        //appController.showAdminEdit(activity);
        activityTitleField.setText(activity.getTitle());
        activityImageField.setText(activity.getImagePath());
        activityConsumptionField.setText(String.valueOf(activity.getConsumption()));
        activitySourceField.setText(activity.getSource());
        selectedActivity = activity;
        // Let user edit, then submit, when submit is clicked, submitEditActivity is called
    }

    /**
     * Initialize the scene
     */
    @Override
    public void initializeController() {
        Activity a = new Activity("",0, "","");
        showEditActivity(a);
    }

    /**
     * Submits edited activity by deleting the original activity and then adding the new version
     */
    public void submitEditActivity() {
        // A new activity will be added with the fields filled in at the moment the user clicks submit
        addActivity();
        appController.showNext();
    }

    /**
     * Implements the functionality of the button with which a user can select an image from their files
     */
    public void chooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select image file");
        File file = fileChooser.showOpenDialog(null);
        String filePath = file.getAbsolutePath();
        String newPath = "src/main/resources/GoodActivities//";
        String ending;
        if (filePath.contains("\\")) {
            ending = filePath.substring(filePath.lastIndexOf('\\') + 1);
        }
        else {
            ending = filePath.substring(filePath.lastIndexOf('/') + 1);
        }
        String completePath = newPath + ending;
        this.sourceFile = new File(filePath);
        this.destinationFile = new File(completePath);
        activityImageField.setText("src/main/resources/GoodActivities//" + ending);
    }
}
