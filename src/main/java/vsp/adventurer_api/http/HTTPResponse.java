package vsp.adventurer_api.http;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Representation of a HTTP-response.
 */
public class HTTPResponse {
    private static Logger LOG = Logger.getLogger(HTTPResponse.class);

    @Nonnull
    private final String json;

    private Gson jsonConverter = new Gson();

    HTTPResponse(@Nonnull String json) {
        this.json = json;
    }

    @Nonnull
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
    @Nullable // TODO make @Nonnull
    public <T> T getAs(@Nonnull final Class<T> type) {
        Preconditions.checkNotNull(type, "type should not be null.");
        LOG.info("Converting to " + type.getSimpleName() + ": \n" + json);
        return jsonConverter.fromJson(json, type);
    }
}