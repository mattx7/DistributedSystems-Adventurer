package vsp.api_client.http.web_resource;

import org.jetbrains.annotations.NotNull;

import static com.google.common.base.Preconditions.checkNotNull;

public class SubResource implements WebResource {

    @NotNull
    private final String path;

    private SubResource(@NotNull MainResource mainResource,
                        @NotNull CharSequence... subPath) {
        this.path = mainResource.getPath()
                .concat("/")
                .concat(String.join("/", subPath));
    }

    public static SubResource from(@NotNull MainResource mainResource,
                                   @NotNull CharSequence... subPath) {
        return new SubResource(
                checkNotNull(mainResource),
                checkNotNull(subPath)
        );

    }

    @NotNull
    public String getPath() {
        return path;
    }
}
