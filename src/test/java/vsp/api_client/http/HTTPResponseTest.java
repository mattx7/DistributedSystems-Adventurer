package vsp.api_client.http;

import com.google.gson.Gson;
import org.testng.annotations.Test;
import vsp.api_client.entities.User;

import static org.testng.AssertJUnit.assertEquals;

public class HTTPResponseTest {

    private User testUser = new User("myName", "1234");
    private final String json = "{ 'name': 'myName', 'password': '1234' }";

    @Test
    public void testGetJson() throws Exception {
        // prepare objects
        final HTTPResponse testObject = new HTTPResponse(json);

        // check method to test
        String result = testObject.getJson();

        // postcondition
        assertEquals(result, json);
    }

    @Test
    public void testGetAs() throws Exception {
        // prepare objects
        final HTTPResponse testObject = new HTTPResponse(json);

        // check method to test
        User result = testObject.getAs(User.class);

        // postcondition
        assertEquals(result, testUser);
    }

    @Test
    public void testGetAsArray() throws Exception {
        // prepare objects
        final HTTPResponse testObject = new HTTPResponse("" +
                "[ { 'name': 'myName', 'password': '1234' }, " +
                "{ 'name': 'myName', 'password': '1234' } ]");

        // check method to test
        User[] result = new Gson().fromJson(testObject.getJson(), User[].class);

        // postcondition
        assertEquals(result, new User[]{testUser, testUser});
    }

}