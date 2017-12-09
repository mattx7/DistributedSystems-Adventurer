package vsp.adventurer_api.http.api;

import org.jetbrains.annotations.NotNull;

public enum OurRoutes implements Route {
    HIRINGS("/hirings"),
    GROUP("/group"),
    ASSIGNMENTS("/assignments"),
    MESSAGES("/messages"),
    ELECTION("/election"),
    RESULTS("/results"),
    JOIN("/join"),
    COORDINATOR("/coordinator");


    private String path;

    OurRoutes(String path) {
        this.path = path;
    }

    @NotNull
    @Override
    public String getPath() {
        return path;
    }
}
