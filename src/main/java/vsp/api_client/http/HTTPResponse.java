package vsp.api_client.http;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Representation of a HTTP-response.
 */
public class HTTPResponse {

    @NotNull
    private final String json;

    private Gson jsonConverter = new Gson();

    HTTPResponse(@NotNull String json) {
        this.json = json;
    }

    @NotNull
    public String getJson() {
        return json;
    }

    /**
     * Returns response as requested type.
     *
     * @param type Not null.
     * @param <T>  Type of the desired object.
     * @return An object of type T. Returns null if response is null or empty.
     */
    @Nullable // TODO make @NotNull
    public <T> T getAs(@NotNull final Class<T> type) {
        Preconditions.checkNotNull(type, "type should not be null.");

        return jsonConverter.fromJson(json, type);
    }

}
