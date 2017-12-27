package vsp.adventurer_api.entities.adventurer;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CreateAdventurerTest {

    @DataProvider(name = "data")
    private Object[][] data() {
        return new Object[][]{
                {new String[]{"group", "selection"}, "group,selection"},
                {new String[]{"group", "group"}, "group"},
        };
    }

    @Test(dataProvider = "data")
    public void testAddCapabilities(String[] capabilities, String expected) throws Exception {
        CreateAdventurer testObject = new CreateAdventurer("wizard", "", "");

        testObject.addCapabilities(capabilities);

        assertEquals(testObject.getCapabilities(), expected);
    }

}