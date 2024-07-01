package server.api;

import commons.Activity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.ActivityRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ActivityController {
    private static ActivityRepository activities;

    public ActivityController(ActivityRepository act) {
        activities = act;
    }

    /**
     * Add an activity to the repository
     * @param act Activity to be added
     * @return true if the activity is successfully added and false otherwise
     */
    public boolean addActivity(Activity act) {
        if(act == null) return false;
        activities.save(act);
        return true;
    }

    /**
     * Post request to add an activity to repository
     * @param activity Activity to be added
     * @return A response of whether the request was successful
     */
    @PostMapping(path = "/activities")
    public ResponseEntity<Activity> addAct(@RequestBody Activity activity) {
        if (activity == null || activity.getTitle() == null
                || activity.getTitle().isEmpty() || activity.getConsumption() == 0 || activity.getConsumption() < 0 || activity.getImagePath() == null
                || activity.getImagePath().isEmpty() || activity.getSource() == null || activity.getSource().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Activity added = activities.save(activity);
        return ResponseEntity.ok(added);
    }

    /**
     * Get request to get all activities in repository
     * @return A list of all activities
     */
    @GetMapping(path = "/data/all")
    public List<Activity> getAllActivities() {
        System.out.println("get all");
        List<Activity> a = activities.findAll();
        Collections.sort(a);
        return a;
    }

    /**
     * Get request to get a random activity from repository
     * @return the randomly chosen activity
     */
    @GetMapping(path = "/data/rand")
    public Activity getRandom() {
        long size = activities.count();
        int idx = (int)(Math.random()*size);
        return activities.findAll().get(idx);
    }

    /**
     * Get request to get all activities from repository with consumption in a certain range
     * @param cons the needed consumption
     * @param range the range of acceptable difference
     * @return the list of all activities in this range
     */
    @GetMapping(path = "/data/fetch/{cons}/{range}")
    public List<Activity> getAllByConsumption(@PathVariable("cons")int cons,@PathVariable("range")int range) {
        return activities.getByConsumption(cons, range);
    }

    /**
     * Get request to get three random activities from repository which have a close consumption
     * @return A list of three activites
     */
    @GetMapping(path = "/data/rand_range")
    public List<Activity> getThreeRandomActivities() {
        List<Activity> act = activities.findAll();
        Collections.sort(act);
        int seed = (int)(Math.random()*act.size());

        List<Activity> filtered = new ArrayList<>();
        filtered.add(act.get(seed));
        while(filtered.size() < 3){
            int rand = (int)(Math.random()*50);
            Activity act_to_add = act.get(0);
            if(seed-25+rand < 0)
                act_to_add = act.get(0);
            else if(seed-25+rand >= act.size())
                act_to_add = act.get(act.size()-1);
            else
                act_to_add = act.get(seed-25+rand);
            boolean sameConsumption = false;
            for(Activity a : filtered){
                if(a.getConsumption() == act_to_add.getConsumption()){
                    sameConsumption=true;
                    break;
                }
            }
            if(sameConsumption) continue;
            if(!filtered.contains(act_to_add)){
                filtered.add(act_to_add);
            }
        }
        return filtered;
    }

    /**
     * Get request to get all activities with a certain consumption from repository
     * @param cons the asked consumption
     * @return a list of all activities with consumption cons
     */
    @GetMapping(path = "/data/fetch/{cons}")
    public List<Activity> getAllByConsumption(@PathVariable("cons")int cons) {
        return activities.getByConsumption(cons, 100);
    }

    /**
     * Get request to get all activities from repository which have a different consumption from cons
     * @param cons the given consumption
     * @return a list of these activities
     */
    @GetMapping(path = "/data/diff/{cons}")
    public List<Activity>getAllDiffCons(@PathVariable("cons")int cons) {
        return activities.getAllDiff(cons, 100);
    }

    /**
     * Post request to delete an activity from repository
     * @param activity the activity to be deleted
     */
    @PostMapping("/activities/delete")
    @Transactional
    public void deleteActivity(@RequestBody Activity activity) {
        List<Activity> list = activities.getByConsumption(activity.getConsumption(),0);
        for (Activity a : list) {
            if (a.equals(activity)) {
                activities.deleteById(a.id);
                break;
            }
        }
    }
}
