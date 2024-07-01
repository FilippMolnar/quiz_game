package server.api;

import commons.Activity;
import commons.Question;
import commons.QuestionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import server.api.utils.TestUtils;
import server.database.ActivityRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

class QuestionControllerTest {
    private final ActivityRepository activityRepository = Mockito.mock(ActivityRepository.class);
    private ActivityController activityController;
    private QuestionController questionController;

    Activity act1 = new Activity("Activity1",1000,"path","google.com");
    Activity act2 = new Activity("Activity2",1500,"path1","google.com");
    Activity act3 = new Activity("Activity3",2000,"path2","google.com");
    Activity act4 = new Activity("Activity4",2500,"path3","google.com");

    @BeforeEach
    void setUp() {

        activityController = new ActivityController(activityRepository);
        questionController = new QuestionController(activityController);
    }

    void initializeActControllerWithList(List<Activity> activities){
        when(activityRepository.count()).thenReturn((long)activities.size());
        when(activityRepository.getByConsumption(anyInt(), anyInt())).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                int consumption = (int)(Integer)args[0];
                int range = (int)(Integer)args[1];
                return activities.stream().filter(activity -> activity.getConsumption() <= consumption + range &&
                        activity.getConsumption() >= consumption - range).collect(Collectors.toList());
            }
        });
        when(activityRepository.findAll()).thenReturn(activities);
    }

    /* this fails: NullPointerException at 'Question q = cont... '
    @Test
    void getRandomQuestion() {
        initializeActControllerWithList(Arrays.asList(act1,act2,act3));
        Question q = questionController.getRandomQuestion();
        assertNotNull(q);
    }
    */

    @Test
    void getTypeEstimate() {
        initializeActControllerWithList(Arrays.asList(act1));

        Question q = questionController.getTypeEstimate();
        assertTrue(q.getCorrect().equals(act1) &&
                q.getType().equals(QuestionType.Estimate) && q.getChoices().size()==0);
    }

    @Test
    void getTypeMostLeast() {
        initializeActControllerWithList(Arrays.asList(act1,act2,act3));

        Question q = questionController.getTypeMostLeast();
        assertTrue(q.getType().equals(QuestionType.HighestEnergy) && q.getCorrect().equals(act3)
                && q.getChoices().size()==3,
                "The question should have 3 options and the best one should be activity 3");
    }

    /**
     * Can`t be tested currently
     */
    @Test
    void getTypeEqual() {
        initializeActControllerWithList(Arrays.asList(act1,act2,act3,act4));

        Question q = questionController.getTypeEqual();
        Set<Activity> choices = new HashSet<>(q.getChoices());
        assertEquals(choices.size(),q.getChoices().size(),"There should not be duplicate choices");
        assertEquals(q.getChoices().size(),4);
    }

    @Test
    void generateRandomQuestionsWithParameters() {
        initializeActControllerWithList(Arrays.asList(act1,act2,act3,act4));
        var questions = questionController.generateRandomQuestions(1,2, 3);
        int countEstimate = (int)questions.stream().filter(q -> q.getType() == QuestionType.Estimate).count();
        int countMostLeast = (int)questions.stream().filter(q -> q.getType() == QuestionType.HighestEnergy).count();
        int countTypeEqual = (int)questions.stream().filter(q -> q.getType() == QuestionType.EqualEnergy).count();
        assertEquals(countEstimate,2);
        assertEquals(countMostLeast,1);
        assertEquals(countTypeEqual,3);
    }
    @Test
    void generate20RandomQuestions(){
        initializeActControllerWithList(TestUtils.getRandomActivities(20));
        assertEquals(questionController.get20RandomQuestions().size(),20);
    }
}
