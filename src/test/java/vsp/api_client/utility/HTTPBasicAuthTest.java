package vsp.api_client.utility;

import org.testng.Assert;
import org.testng.annotations.Test;
import vsp.api_client.http.auth.HTTPBasicAuth;

public class HTTPBasicAuthTest {

    /**
     * Tests {@link HTTPBasicAuth#getAuthHeader()}.
     */
    @Test
    public void test() throws Exception {
        // prepare objects
        HTTPBasicAuth auth = new HTTPBasicAuth("name", "pass");
        // check method to test
        final String authHeaderInBase64 = auth.getAuthHeader();
        // post-conditions
        Assert.assertEquals(authHeaderInBase64, "Basic bmFtZTpwYXNz"); // expected from https://www.base64encode.org/
    }

}
