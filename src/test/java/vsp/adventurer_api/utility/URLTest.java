package vsp.adventurer_api.utility;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class URLTest {

    @DataProvider(name = "data")
    private Object[][] data() {
        return new Object[][]{
                {"1.2.3.4:1234", new URL("1.2.3.4", 1234)},
                {"http://1.2.3.4:1234", new URL("1.2.3.4", 1234)},
                {"http://1.2.3.4:1234/", new URL("1.2.3.4", 1234)},
                {"1.2.3.4:1234/route", new URL("1.2.3.4", 1234, "/route")},
                {"1.2.3.4:1234/route/route2", new URL("1.2.3.4", 1234, "/route/route2")},
                {"1.2.3.4:1234/route/route2/route3", new URL("1.2.3.4", 1234, "/route/route2/route3")},
        };
    }

    @Test(dataProvider = "data")
    public void testParseURL(String request, URL expected) throws Exception {
        URL result = URL.parse(request);
        assertEquals(result.getAddress(), expected.getAddress());
        assertEquals(result.getPort(), expected.getPort());
        assertEquals(result.getRoute(), expected.getRoute());
    }
}