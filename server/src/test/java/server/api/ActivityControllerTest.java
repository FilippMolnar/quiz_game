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
package server.api;

import commons.Activity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import server.api.utils.TestUtils;
import server.database.ActivityRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ActivityControllerTest {

    private final ActivityRepository mockActivityRepo = Mockito.mock(ActivityRepository.class);;
    private ActivityController sut;
	Activity activity = new Activity("Activity", 100, "/path/to/img", "google.com");

    @BeforeEach
    public void setup() {
        reset(mockActivityRepo);
        when(mockActivityRepo.findAll()).thenReturn(Arrays.asList(activity));
        when(mockActivityRepo.findById(any(Long.class))).
                thenReturn(Optional.of(activity));
        sut = new ActivityController(mockActivityRepo);
    }

    @Test
    public void cannotAddNull() {
        assertFalse(sut.addActivity(null));
    }

    @Test
    public void getRandomTest() {
        sut.addActivity(activity);
        var actual = sut.getRandom();
        assertEquals(actual,activity);
    }

    @Test
    public void addActivity() {
        sut.addActivity(activity);
        verify(mockActivityRepo,times(1)).save(activity);
    }

    @Test
    public void getAllActivities(){
        Activity activity1 = new Activity("Activity", 100, "/path/to/img", "google.com");
        Activity activity2 = new Activity("Activity", 120, "/path/to/img", "google.com");
        Activity activity3 = new Activity("Activity", 130, "/path/to/img", "google.com");
        when(mockActivityRepo.findAll()).thenReturn(Arrays.asList(activity1, activity2, activity3));
        var activities = sut.getAllActivities();
        for(int i = 0; i + 1 < activities.size(); i++){
            assertTrue(activities.get(i).getConsumption() >
                    activities.get(i + 1).getConsumption(), "Activities should be sorted decreasingly ");
        }
        verify(mockActivityRepo,times(1)).findAll();
    }
    private List<Activity> get15RandomActivities(){
        List<Activity> activities = new ArrayList<>();
        for(int i = 1; i < 15; i++){
            activities.add(TestUtils.getRandomActivity());
        }
        return activities;
    }


    @Test
    public void getAllByConsumption(){
        sut.getAllByConsumption(50);
        verify(mockActivityRepo,times(1)).getByConsumption(eq(50), any(Integer.class));
    }

    @Test
    public void getAllDiffCons(){
        sut.getAllDiffCons(50);
        verify(mockActivityRepo,times(1)).getAllDiff(eq(50),any(Integer.class));
    }

    @Test
    public void getThreeRandom(){
        List<Activity> activities = get15RandomActivities();
        when(sut.getAllActivities()).thenReturn(activities);
        // check if we get 3 distinct activities every time
        for(int i = 1; i < 15; i++){
            var returnedList = sut.getThreeRandomActivities();
            // the activities have to be distinct
            assertEquals(returnedList.size(),3,"There should be 3 random activities");
            Set<Activity> activitySet = new HashSet<>(returnedList);
            assertEquals(activitySet.size(),3,"The activities should be unique");
        }
    }
    @Test
    public void deleteExistingActivity(){
        List<Activity> activities = get15RandomActivities();
        when(sut.getAllByConsumption(any(Integer.class),any(Integer.class)))
                .thenReturn(activities);
        sut.deleteActivity(activities.get(0));
        verify(mockActivityRepo,times(1)).deleteById(activities.get(0).id);
    }
    @Test
    public void deleteNonExistingActivity(){
        when(sut.getAllByConsumption(any(Integer.class),any(Integer.class)))
                .thenReturn(new ArrayList<>());
        sut.deleteActivity(activity);
        verify(mockActivityRepo,times(0)).deleteById(any(Long.class));
    }

    @Test
    public void addActivityFromClient(){
        assertEquals(sut.addAct(activity).getStatusCode(), HttpStatus.OK);
        assertEquals(sut.addAct(null).getStatusCode(), HttpStatus.BAD_REQUEST);
        assertEquals(sut.addAct( new Activity("", 100, "/path/to/img", "google.com")).getStatusCode(),
                HttpStatus.BAD_REQUEST,"title should not be empty");
        assertEquals(sut.addAct( new Activity("Activity", 0, "/path/to/img", "google.com")).getStatusCode(),
                HttpStatus.BAD_REQUEST,"Consumption should not be 0");
        assertEquals(sut.addAct( new Activity("Activity", -10, "/path/to/img", "google.com")).getStatusCode(),
                HttpStatus.BAD_REQUEST,"Consumption should not be negative");
        assertEquals(sut.addAct( new Activity("Activity", 12, "", "google.com")).getStatusCode(),
                HttpStatus.BAD_REQUEST, "Img path should not be null");
        assertEquals(sut.addAct( new Activity("Activity", 130, "/path/to/img", "")).getStatusCode(),
                HttpStatus.BAD_REQUEST, "Source should not be null");
    }

}
