package vsp.adventurer_api.http.api;

import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

public class SubPath implements Route {

    @NotNull
    private final String path;

    private SubPath(@NotNull BlackboardRoutes mainResource,
                    @NotNull CharSequence... subPath) {
        this.path = mainResource.getPath()
                .concat("/")
                .concat(String.join("/", subPath));
    }

    public static SubPath from(@NotNull BlackboardRoutes mainResource,
                               @NotNull CharSequence... subPath) {
        return new SubPath(
                checkNotNull(mainResource),
                checkNotNull(subPath)
        );

    }

    @NotNull
    public String getPath() {
        return path;
    }
}
