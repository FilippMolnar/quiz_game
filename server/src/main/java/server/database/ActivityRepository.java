package server.database;

import commons.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ActivityRepository extends JpaRepository<Activity, Long> {
    /**
     * Finds all activities which have a consumption ranging from cons-range to cons+range
     *
     * @param cons the consumption which we seek
     * @return a list of activities which has a specific consumption
     */
    @Query("SELECT a FROM Activity a WHERE a.consumption>=?1-?2 AND a.consumption<=?1+?2")
    List<Activity> getByConsumption(int cons, int range);

    /**
     * Fetches all activities having consumption different from the range cons-range to cons+range
     *
     * @param cons
     * @return
     */
    @Query("SELECT a FROM Activity a WHERE a.consumption>?1+?2 OR a.consumption<?1-?2")
    List<Activity> getAllDiff(int cons, int range);


}
