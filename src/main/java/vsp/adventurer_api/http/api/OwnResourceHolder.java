package vsp.adventurer_api.http.api;

import org.jetbrains.annotations.NotNull;

public enum OwnResourceHolder implements ResourceHolder {
    HIRINGS("/hirings"), GROUP("/group"), ASSIGNMENTS("/assignments"), MESSAGES("/messages");

    private String path;

    OwnResourceHolder(String path) {
        this.path = path;
    }

    @NotNull
    @Override
    public String getPath() {
        return path;
    }
}
