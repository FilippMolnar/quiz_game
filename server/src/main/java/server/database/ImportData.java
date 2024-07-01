package server.database;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Activity;
import commons.TemplateActivity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.api.ActivityController;

import java.io.File;
import java.util.*;

@RestController
@RequestMapping("/api/data")
public class ImportData {

    ActivityController activityController;

    ImportData(ActivityController activityController){
        this.activityController = activityController;
    }
    /**
     * Gets the base name, without extension, of given file name.
     * <p/>
     * e.g. getBaseName("file.txt") will return "file"
     *
     * @param fileName
     * @return the base name
     */
    public static String getBaseName(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index == -1) {
            return fileName;
        } else {
            return fileName.substring(0, index);
        }
    }

    /**
     *  Import a given activity to the database
     * @param jsonPath the path to the json file
     * @param imagePath the path to the image file
     * @param groupName the number of the group which suggested the activity
     */
    private void addActivityToDb(File jsonPath, File imagePath, String groupName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Read the activity from JSON file
            TemplateActivity temp = mapper.readValue(jsonPath, TemplateActivity.class);
            // Create a new activity to add to database
            Activity act = new Activity(temp.title, temp.consumption_in_wh, groupName + "/" + imagePath.getName(), temp.getSource());
            // Add activity to database
            activityController.addActivity(act);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Imports all activities from the given folder to database
     * @return a prompt that the loading was successful
     */
    @GetMapping(path = "/load")
    public String importAllFiles() {
        String resourceFolder = "src/main/resources/GoodActivities";
        File dir = new File(resourceFolder);
        File[] directories = dir.listFiles(File::isDirectory);
        if (directories == null) {
            return "<h3>The server cannot find the file specified " + resourceFolder + "</h3>\n<p>Try using server/main/resources or " +
                    "main/resources </p>";
        }
        for (File directoryPath : directories) {
            // groupName will be 33 or 13 basically the group number
            String groupName = directoryPath.getName();
            File[] listing = directoryPath.listFiles();
            if (listing == null) {
                return "The path " + directoryPath + " does not have subdirectories";
            }
            // map String 33/Freezer -> [Freezer.jpg,Freezer.json]
            Map<String, List<File>> map = new HashMap<>();
            for (File file : listing) {
                String path = getBaseName(file.getPath()); // base path just ignores the extension
                System.out.println("Path is :" + path);
                var listBefore = map.getOrDefault(path, new ArrayList<>());
               listBefore.add(file);
                map.put(path, listBefore);
            }
            for (var mapEntry : map.entrySet()) {
                String path = mapEntry.getKey(); // 33/Freezer
                List<File> files = mapEntry.getValue(); // [Freezer.jpg,Freezer.json]
                // each pair consists of [image,json] or [json,image] I don't know the order
                // I have to first get the image and then the json
                Optional<File> imgPath = files.stream().filter(f -> !f.getName().endsWith(".json")).findFirst();
                if (imgPath.isEmpty()) {
                    return "There is a json without an image associated.For path:" + path + " we have : " + files;
                }
                File imagePath = imgPath.get(); // get the image for this pair
                for (File jsonPath : files)
                    if (jsonPath.getName().endsWith(".json"))
                        addActivityToDb(jsonPath, imagePath, groupName);
            }
        }
        return "Data from all activities was loaded in database!";
    }
}
