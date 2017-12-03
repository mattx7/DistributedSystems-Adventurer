package vsp.adventurer_api.http.api;

import org.jetbrains.annotations.NotNull;

/**
 * Offers all possible resources from the rest api.
 */
public enum MainResourceHolder implements ResourceHolder {
    ADVENTURERS("/taverna/adventurers"), USERS("/users"), LOGIN("/login");

    @NotNull
    private String path;

    MainResourceHolder(@NotNull final String path) {
        this.path = path;
    }

    @NotNull
    public String getPath() {
        return path;
    }
}