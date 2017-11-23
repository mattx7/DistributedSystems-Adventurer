package vsp.api_client.http.web_resource;

import org.jetbrains.annotations.NotNull;

/**
 * Representation of a RESTful resource.
 */
public interface WebResource {

    /**
     * Return url path to the web resource.
     *
     * @return Not null.
     */
    @NotNull
    String getPath();

}
