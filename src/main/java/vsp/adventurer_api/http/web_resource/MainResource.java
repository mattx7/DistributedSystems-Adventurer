package vsp.adventurer_api.http.web_resource;

import org.jetbrains.annotations.NotNull;

/**
 * Offers all possible resources from the rest api.
 */
public enum MainResource implements WebResource {
    ADVENTURERS("/taverna/adventurers"), USERS("/users"), LOGIN("/login");

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