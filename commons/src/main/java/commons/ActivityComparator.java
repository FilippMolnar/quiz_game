package commons;

import java.util.Comparator;

public class ActivityComparator implements Comparator<Activity> {

    /**
     * Compares two activities
     * @param a1 - the first activity to compare
     * @param a2 - the second activity to compare
     * @return an integer,
     *        -1 if this is greater than the input activity
     *        0 if both are equal
     *        1 if the input activity is greater
     */
    public int compare(Activity a1, Activity a2) {
        return a1.compareTo(a2);
    }
}
