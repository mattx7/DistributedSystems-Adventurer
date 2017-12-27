package vsp.adventurer_api.utility;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class LamportClockTest {

    private LamportClock lamportClock;

    @BeforeMethod
    public void setUp() throws Exception {
        lamportClock = new LamportClock();
        lamportClock.getAndIncrease();
        lamportClock.getAndIncrease();
        lamportClock.getAndIncrease();
    }

    @DataProvider(name = "testGetData")
    private Object[][] testGetData() {
        return new Object[][]{
                {1, 4},
                {5, 6},
        };
    }

    @Test(dataProvider = "testGetData")
    public void testGet(int request, int expected) throws Exception {
        int result = lamportClock.compareAndIncrease(request);

        assertEquals(result, expected);
    }

}