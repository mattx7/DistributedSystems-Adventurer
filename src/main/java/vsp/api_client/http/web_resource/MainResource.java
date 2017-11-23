package vsp.api_client.http.web_resource;

import org.jetbrains.annotations.NotNull;

/**
 * Offers all possible resources from the rest api.
 */
public enum MainResource implements WebResource {
    USERS("/users"), LOGIN("/login"), WHOAMI("/whoami"), QUESTS("/blackboard/quests"), MAP("/map");

    @NotNull
    private String path;

    MainResource(@NotNull final String path) {
        this.path = path;
    }

    @NotNull
    public String getPath() {
        return path;
    }
}
