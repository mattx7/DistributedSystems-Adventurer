package vsp.adventurer_api.http.api;

import org.jetbrains.annotations.NotNull;

/**
 * Representation of a RESTful resource.
 */
public interface ResourceHolder {

    /**
     * Return url path to the web resource.
     *
     * @return Not null.
     */
    @NotNull
    String getPath();

}
