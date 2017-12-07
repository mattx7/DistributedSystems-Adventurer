package vsp.adventurer_api.entities.adventurer;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class CreateAdventurerTest {

    @Test
    public void testAddCapabilities() throws Exception {
        CreateAdventurer testObject = new CreateAdventurer("wizard", "", "");

        testObject.addCapabilities("group", "selection");

        assertEquals(testObject.getCapabilities(), "group,selection");
    }

}