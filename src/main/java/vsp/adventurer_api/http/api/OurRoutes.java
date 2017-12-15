package vsp.adventurer_api.http.api;


import javax.annotation.Nonnull;

/**
 * Our intern routes for the rest API.
 */
public class OurRoutes implements Route {
    public static final String HIRINGS = "/hirings";
    public static final String GROUP = "/group";
    public static final String ASSIGNMENTS = "/assignments";
    public static final String MESSAGES = "/messages";
    public static final String ELECTION = "/election";
    public static final String RESULTS = "/results";
    public static final String JOIN = "/join";
    public static final String COORDINATOR = "/coordinator";


    private String path;

    OurRoutes(String path) {
        this.path = path;
    }

    @Nonnull
    @Override
    public String getPath() {
        return path;
    }
}
