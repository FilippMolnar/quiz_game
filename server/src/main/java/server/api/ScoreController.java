package server.api;

import commons.Score;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import server.database.ScoreRepository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/api/score")
public class ScoreController {

    private final ScoreRepository repo;

    public ScoreController(ScoreRepository scoreRepository) {
        this.repo = scoreRepository;
    }

    /**
     * Get leaderboard from database
     * @return a list of scores in the leaderboard
     */
    @GetMapping(path = { "", "/" })
    public List<Score> getAll() {
        return repo.getLeaderboard();
    }

    /**
     * Add a score to the leaderboard in database
     * @param score The score to be added to leaderboard
     */
    @PostMapping(path = { "", "/" })
    public void add(@RequestBody Score score) {
        repo.save(score);
    }

}
