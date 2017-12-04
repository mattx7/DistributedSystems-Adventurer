package vsp.adventurer_api.http.api;

import org.jetbrains.annotations.NotNull;

/**
 * Offers all possible resources from the rest api.
 */
public enum BlackboardRoutes implements Route {
    ADVENTURERS("/taverna/adventurers"), USERS("/users"), LOGIN("/login"), WHOAMI("/whoami"), QUESTS("/quests"), MAP("/map"), GROUP("/taverna/groups"), VISITS("/visits");

    @NotNull
    private String path;

    BlackboardRoutes(@NotNull final String path) {
        this.path = path;
    }

    @NotNull
    public String getPath() {
        return path;
    }
}