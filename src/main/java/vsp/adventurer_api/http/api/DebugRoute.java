package vsp.adventurer_api.http.api;

import org.jetbrains.annotations.NotNull;

public class DebugRoute implements Route {

    @NotNull
    private final String path;

    public DebugRoute(@NotNull final String path) {
        this.path = path;
    }

    @NotNull
    @Override
    public String getPath() {
        return path;
    }
}
