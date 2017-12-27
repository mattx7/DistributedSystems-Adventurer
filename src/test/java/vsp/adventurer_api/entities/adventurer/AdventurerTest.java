package vsp.adventurer_api.entities.adventurer;

import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class AdventurerTest {

    @Test
    public void testHasCapability() throws Exception {
        Adventurer testObject = new Adventurer("a,b, c,d", "", "", "");
        assertTrue(testObject.hasCapability("a"));
        assertTrue(testObject.hasCapability("b"));
        assertTrue(testObject.hasCapability("c"));
        assertTrue(testObject.hasCapability("d"));
    }
}