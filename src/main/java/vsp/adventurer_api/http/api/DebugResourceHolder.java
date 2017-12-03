package vsp.adventurer_api.http.api;

import org.jetbrains.annotations.NotNull;

public class DebugResourceHolder implements ResourceHolder {

    @NotNull
    private final String path;

    public DebugResourceHolder(@NotNull final String path) {
        this.path = path;
    }

    @NotNull
    @Override
    public String getPath() {
        return path;
    }
}
