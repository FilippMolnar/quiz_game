package commons;

import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Data
public class Question{

    private List<Activity> choices;
    private QuestionType type;
    private Activity correct;

    /**
     * An empty constructor for a Question
     */
    public Question(){}

    /**
     * Constructor for a Question
     */
    public Question(Activity correct, List<Activity> choices, QuestionType type) {
        this.type = type;
        this.choices = choices;
        this.correct = correct;

        // sorts in descending order
        // CHOICES.sort(new ActivityComparator());
        this.choices.sort(Comparator.naturalOrder());
    }

    /**
     * Checks if the given activity is the correct one
     * @param a - the given activity
     * @return if the activity is the correct one (true) or not (false)
     */
    public Boolean isCorrect(Activity a) {
        return a.equals(correct);
    }

    /**
     * Compares if o is equal to this instance
     * @param o - the object we are comparing
     * @return if this is equal to o
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Question question = (Question) o;
        return choices.equals(question.choices) && type == question.type && correct.equals(question.correct);
    }

    @Override
    public int hashCode() {
        return Objects.hash(choices, type, correct);
    }

    /**
     * @return Human-readable format of this Class
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Question type: ");
        str.append(type);
        str.append("\nCorrect answer:\n");
        str.append(correct.toString());
        str.append("\nOptions:\n");
        for(Activity choice : choices) {
            str.append(choice.toString());
            str.append("\n");
        }
        return str.toString();
    }
}
