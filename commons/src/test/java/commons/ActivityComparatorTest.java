package commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ActivityComparatorTest {
    @Test
    public void testCompare() {
        Activity a = new Activity("Activity", 100, "/path/to/img", "google.com");
        Activity b = new Activity("A", 1, "/path/to/img", "google.com");
        Activity c = new Activity("A", 1000, "/path/to/img", "google.com");
        Activity d = new Activity("A", 100, "/path/to/img", "google.com");

        ActivityComparator comparator = new ActivityComparator();
        assertEquals(comparator.compare(a, b), -1);
        assertEquals(comparator.compare(a, c), 1);
        assertEquals(comparator.compare(a, d), 0);
    }
}
