package server.database;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import server.api.ActivityController;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImportDataTest {

    ActivityController activityController = Mockito.mock(ActivityController.class);
    ImportData data = new ImportData(activityController);

    @Test
    void getBaseName() {
        String file = "/file/to/here.txt";
        assertEquals(ImportData.getBaseName(file),"/file/to/here");
        String fileReverse = "\\file\\to\\here";
        assertEquals(ImportData.getBaseName(fileReverse),"\\file\\to\\here");
    }


}