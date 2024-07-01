/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package commons;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuestionTest {
    Activity a = new Activity("Activity1", 100, "/path/to/img", "google.com");
    Activity b = new Activity("Activity2", 200, "/path/to/img", "google.com");
    Activity c = new Activity("Activity3", 300, "/path/to/img", "google.com");

    Question q = new Question(c, Arrays.asList(a, b, c), QuestionType.HighestEnergy);

    @Test
    public void constructorTest() {
        assertNotNull(q);
    }

    @Test
    public void setChoicesTest()
    {
        Question q = new Question();
        List<Activity> ls = new ArrayList<>();
        ls.add(a);
        ls.add(c);
        q.setChoices(ls);
        assertTrue(q.getChoices().equals(ls));
    }

    @Test
    public void setTypeTest()
    {
        Question q = new Question();
        q.setType(QuestionType.HighestEnergy);
        assertEquals(q.getType(), QuestionType.HighestEnergy);
    }

    @Test
    public void setCorrectTest()
    {
        Question q = new Question();
        q.setCorrect(a);
        assertEquals(q.getCorrect(), a);
    }

    @Test
    public void getterTest() {
        assertEquals(QuestionType.HighestEnergy, q.getType());
        assertEquals(c, q.getCorrect());
        assertEquals(Arrays.asList(c, b, a), q.getChoices());
    }

    @Test
    public void correctTest() {
        assertTrue(q.isCorrect(c));
    }

    @Test
    public void notCorrectTest() {
        assertFalse(q.isCorrect(b));
    }

    @Test
    public void compareTest() {
        Activity b = new Activity("Activity", 200, "/path/to/img", "google.com");
        assertEquals(1, a.compareTo(b));
        assertEquals(-1, b.compareTo(a));
        assertEquals(0, a.compareTo(a));
    }

    @Test
    public void sortTest() {
        assertNotEquals(q.getChoices(), Arrays.asList(a, b, c));
    }

    @Test
    public void toStringTest() {
        assertEquals("Question type: HighestEnergy\nCorrect answer:\n" + c.toString()
                + "\nOptions:\n" + c.toString() + "\n" + b.toString() + "\n"
                + a.toString() + "\n", q.toString());
    }

    @Test
    public void hashTest()
    {
        Question q = new Question();
        assertEquals(q.hashCode(), 29791);
    }

    @Test
    public void equalsTest()
    {
        Activity a = new Activity("Activity1", 100, "/path/to/img", "google.com");
        Activity b = new Activity("Activity2", 200, "/path/to/img", "google.com");
        Activity c = new Activity("Activity3", 300, "/path/to/img", "google.com");

        Activity a1 = new Activity("Activity1", 100, "/path/to/img", "google.com");
        Activity b1 = new Activity("Activity2", 200, "/path/to/img", "google.com");
        Activity c1 = new Activity("Activity3", 300, "/path/to/img", "google.com");

        List<Activity> ls = new ArrayList<>();
        ls.add(a);
        ls.add(b);
        ls.add(c);
        Question q = new Question(c, ls, QuestionType.HighestEnergy);

        List<Activity> ls1 = new ArrayList<>();
        ls1.add(a1);
        ls1.add(b1);
        ls1.add(c1);
        Question q1 = new Question(c1, ls1, QuestionType.HighestEnergy);

        assertEquals(q, q);
        assertFalse(q.equals(null));
        assertFalse(q.equals(new String("s")));

        assertTrue(q.equals(q1));

        q1 = new Question(b1, ls1, QuestionType.HighestEnergy);
        assertFalse(q.equals(q1));

        q1 = new Question(c1, new ArrayList<>(), QuestionType.HighestEnergy);
        assertFalse(q.equals(q1));

        q1 = new Question(c1, ls1, QuestionType.EqualEnergy);
        assertFalse(q.equals(q1));

    }

}
