package vsp.adventurer_api.http.api;

import org.apache.commons.lang3.StringUtils;

/**
 * Representation of a REST resource/route.
 */
public interface Route {

    /**
     * Combines given arguments with "/"
     */
    static String concat(String... routes) {
        return StringUtils.join(routes, "/");
    }
}
