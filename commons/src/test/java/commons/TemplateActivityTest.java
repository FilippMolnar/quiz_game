package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TemplateActivityTest {
    TemplateActivity temp = new TemplateActivity("Name", 100 , "Source");
    @Test
    void constructorTest() {
        assertNotNull(temp);
    }

    @Test
    void getTitle() {
        assertEquals(temp.getTitle(),"Name");
    }

    @Test
    void setTitle() {
        temp.setTitle("NewName");
        assertEquals(temp.getTitle(),"NewName");
    }

    @Test
    void getConsumptionInWh() {
        assertEquals(temp.getConsumptionInWh(),100);
    }

    @Test
    void setConsumptionInWh() {
        temp.setConsumptionInWh(200);
        assertEquals(temp.getConsumptionInWh(),200);
        assertNotEquals(temp.getConsumptionInWh(),2000);
    }

    @Test
    void getSource()
    {
        assertTrue(temp.getSource().equals("Source"));
        assertFalse(temp.getSource().equals("source"));
    }

    @Test
    void setSource()
    {
        temp.setSource("source");
        assertTrue(temp.getSource().equals("source"));
        assertFalse(temp.getSource().equals("Source"));
    }

    @Test
    void testEquals() {
        TemplateActivity a = new TemplateActivity("Name" , 100 , "Source");
        assertTrue(a.equals(a));
        assertFalse(a.equals("s"));

        TemplateActivity b = new TemplateActivity("N" , 100 , "Source");
        assertFalse(a.equals(b));

        b = new TemplateActivity("Name" , 0 , "Source");
        assertFalse(a.equals(b));

        b = new TemplateActivity("Name" , 100 , "S");
        assertFalse(a.equals(b));

        b = new TemplateActivity("Name" , 100 , "Source");
        assertTrue(a.equals(b));
    }

    @Test
    void toStringTest()
    {
        String exp = "TemplateActivity{" +
                "title='" + temp.getTitle() + '\'' +
                ", consumption_in_wh=" + temp.getConsumptionInWh() +
                ", source='" + temp.getSource() + '\'' +
                '}';
        assertTrue(exp.equals(temp.toString()));
    }
}