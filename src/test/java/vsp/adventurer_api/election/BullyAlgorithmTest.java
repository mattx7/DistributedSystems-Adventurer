package vsp.adventurer_api.election;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class BullyAlgorithmTest {

    @Test
    public void test() throws Exception {
        BullyAlgorithm algorithm = new BullyAlgorithm();

        long result = algorithm.nextId();

        assertEquals(result, 1L);
    }

}