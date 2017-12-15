package vsp.adventurer_api.http.api;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;

/**
 * Representation of a REST resource/route.
 */
public interface Route {

    /**
     * Return url path to the web resource.
     *
     * @return Not null.
     */
    @Nonnull
    String getPath();

    /**
     * Combines given arguments with "/"
     */
    static String concat(String... routes) {
        return StringUtils.join(routes, "/");
    }
}
