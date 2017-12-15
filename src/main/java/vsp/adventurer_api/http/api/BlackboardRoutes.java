package vsp.adventurer_api.http.api;


import javax.annotation.Nonnull;

/**
 * Offers all possible resources from the rest api.
 */
public class BlackboardRoutes implements Route {
    public static final String ADVENTURERS = "/taverna/adventurers";
    public static final String USERS = "/users";
    public static final String LOGIN = "/login";
    public static final String WHOAMI = "/whoami";
    public static final String QUESTS = "/blackboard/quests";
    public static final String MAP = "/map";
    public static final String GROUP = "/taverna/groups";
    public static final String VISITS = "/visits";

    @Nonnull
    private String path;

    BlackboardRoutes(@Nonnull final String path) {
        this.path = path;
    }

    @Nonnull
    public String getPath() {
        return path;
    }
}