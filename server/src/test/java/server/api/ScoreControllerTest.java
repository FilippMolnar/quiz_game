package server.api;

import commons.Score;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import server.database.ScoreRepository;

import static org.mockito.Mockito.verify;

class ScoreControllerTest {

    ScoreRepository scoreRepository = Mockito.mock(ScoreRepository.class);
    ScoreController scoreController = new ScoreController(scoreRepository);
    @Test
    void getAll() {
        scoreController.getAll();
        verify(scoreRepository).getLeaderboard();
    }

    @Test
    void add() {
        scoreController.add(new Score("Izzy", 10));
        verify(scoreRepository).save(new Score("Izzy", 10));
    }
}