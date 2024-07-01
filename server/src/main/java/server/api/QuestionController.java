package server.api;

import commons.Activity;
import commons.Question;
import commons.QuestionType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping(path = "/api")
public class QuestionController {

    private ActivityController activityController;

    public QuestionController(ActivityController activityController) {
        this.activityController = activityController;
    }

    /**
     * Get a certain amount of random questions of each type
     * @param typeMostLeast Num of questions of type most
     * @param typeMostEstimate Num of questions of type estimate
     * @param typeEqual Num of questions of type equal
     * @return The list of randomly generated questions
     */
    public List<Question> generateRandomQuestions(int typeMostLeast,int typeMostEstimate, int typeEqual){
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i < typeMostLeast; i++)
            questions.add(getTypeMostLeast());
        for (int i = 0; i < typeMostEstimate; i++)
            questions.add(getTypeEstimate());
        for (int i = 0; i < typeEqual; i++)
            questions.add(getTypeEqual());
        Collections.shuffle(questions);
        return questions;
    }

    /**
     * Get 20 random questions
     * @return The list of generated questions
     */
    @GetMapping("/generate_20")
    public List<Question> get20RandomQuestions() {
        return generateRandomQuestions(9,6,5);
    }

    /**
     * Get a random question of random type
     * @return Generated question
     */
    @GetMapping("/question")
    public Question getRandomQuestion() {
        int pick = new Random().nextInt(3);
        if (pick == 0)
            return getTypeEstimate();
        else if (pick == 1)
            return getTypeEqual();
        return getTypeMostLeast();
    }

    /**
     * Fetches data and constructs a question of type estimate
     *
     * @return question of type estimate    !!!    CHOICES ARE AN EMPTY LIST  !!!
     */
    @GetMapping(path = {"/estimate"})
    public Question getTypeEstimate() {
        Activity act = activityController.getRandom();
        Question q = new Question();
        q.setChoices(new ArrayList<>());
        q.setType(QuestionType.Estimate);
        q.setCorrect(act);

        return q;
    }

    /**
     * Fetches data and constructs a question of type most/least
     *
     * @return question of type most/least  !!!     CORRECT ANSWER IS NOT SET   !!!
     */
    @GetMapping(path = {"/most"})
    public Question getTypeMostLeast() {
        List<Activity> choices = activityController.getThreeRandomActivities();

        Activity highest = choices.get(0);
        for(Activity a : choices){
            highest = highest.getConsumption() > a.getConsumption() ? highest : a;
        }
        Collections.shuffle(choices);
        Question q = new Question();
        q.setCorrect(highest);
        q.setType(QuestionType.HighestEnergy);
        q.setChoices(choices);
        return q;
    }

    /**
     * Fetches data and constructs a question of type equal
     *
     * @return question of type equal  !!!    OPTIMIZED VERSION   !!!
     */
    @GetMapping(path = {"/equal"})
    public Question getTypeEqual() {
        // get a random activity
        Activity act = activityController.getRandom();
        List<Activity> same = activityController.getAllByConsumption(act.getConsumption(),100);
        List<Activity> choices = new ArrayList<>();
        //Activity neither = new Activity("neither", -1, "location of cross");
        Activity neither = new Activity("Neither of these", -1, "79/cross.png", "");

        if (same.size() == 1) same.add(neither);

        int idx = (int) (Math.random() * same.size());
        if (same.get(idx).equals(act)) {
            idx++;
            idx %= (same.size());
        }
        Question q = new Question();
        q.setCorrect(same.get(idx));

        choices.add(same.get(idx));

        if (!choices.contains(neither)) {
            int chance = (int) (Math.random() * 100);
            if (chance <= 33) {
                choices.add(neither);
            }
        }

        while (choices.size() < 3) {
            Activity choice = activityController.getRandom();
            if (same.contains(choice) || act.equals(choice) || choices.contains(choice)) continue;
            choices.add(choice);
        }
        Collections.shuffle(choices);
        choices.add(act);
        q.setType(QuestionType.EqualEnergy);
        q.setChoices(choices);
        return q;
    }

}
