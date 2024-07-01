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

import static org.junit.jupiter.api.Assertions.*;

public class ActivityTest {
	Activity a = new Activity("Activity", 100, "/path/to/img", "google.com");

	@Test
	public void constructorTest() {
		assertNotNull(a);
	}

	@Test
	public void getterTest() {
		assertEquals("Activity", a.getTitle());
		assertEquals(100, a.getConsumption());
		assertEquals("/path/to/img", a.getImagePath());
		assertEquals("google.com", a.getSource());

	}

	@Test
	public void equalsTest() {
        Activity b = new Activity("Activity", 100, "/path/to/img", "google.com");
        assertEquals(a, b);
	}

	@Test
	public void notEqualsTest() {

		Activity b = new Activity("Activity", 200, "/path/to/img", "google.com");
		Activity c = new Activity("Activity", 200, "/path/to/img", null);
		Activity d = new Activity("Activity", 200, "/path/to/imgs", "google.com");
		Activity e = new Activity("Activity", 200, "/path/to/img", "google.sk");
		assertNotEquals(a, b);
		assertFalse(b.equals(null));
		assertFalse(b.equals("string")); // not instance of activity
		assertFalse(c.equals(b)); //no source
		assertFalse(b.equals(d));
		assertFalse(b.equals(e));

	}

	@Test
	public void compareTest() {
        Activity b = new Activity("Activity", 200, "/path/to/img", "google.com");
        assertEquals(1, a.compareTo(b));
        assertEquals(-1, b.compareTo(a));
        assertEquals(0, a.compareTo(a));
		assertThrows(IllegalArgumentException.class, () -> b.compareTo("string"));
	}

    @Test
    public void toStringTest() {
        assertEquals(a.toString(), "Activity:\n100 WH\n/path/to/img");
    }
}
