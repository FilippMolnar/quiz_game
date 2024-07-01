package server.api.utils;

import commons.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestUtils {

    public static Activity getRandomActivity(){
        int randomConsumption = new Random().nextInt() % (1000);
        return new Activity("Activity", randomConsumption, "/path/to/img", "google.com");
    }

    public static List<Activity> getRandomActivities(int numberOfActivities){
        List<Activity> activities = new ArrayList<>();
        for(int i = 1; i < numberOfActivities; i++){
            activities.add(TestUtils.getRandomActivity());
        }
        return activities;
    }
    public static List<Activity> get15RandomActivities(){
        return getRandomActivities(15);
    }
}
