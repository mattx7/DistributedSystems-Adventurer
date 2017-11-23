package vsp.api_client.http.web_resource;

import org.jetbrains.annotations.NotNull;

public class DebugResource implements WebResource {

    @NotNull
    private final String path;

    public DebugResource(@NotNull final String path) {
        this.path = path;
    }

    @NotNull
    @Override
    public String getPath() {
        return path;
    }
}
