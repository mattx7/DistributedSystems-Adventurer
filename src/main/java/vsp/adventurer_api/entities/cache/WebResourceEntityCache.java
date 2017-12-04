package vsp.adventurer_api.entities.cache;

import vsp.adventurer_api.http.api.OurRoutes;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Holds all objects that should be visible as resource. <b>No DB needed because this application runs in a container.</b>
 */
public class WebResourceEntityCache<T> {

    @Nonnull
    private OurRoutes webResource;

    @Nonnull
    private final List<T> objects = new ArrayList<>();

    @Nonnull
    private final Class<T> clazz;

    public WebResourceEntityCache(@Nonnull final Class<T> clazz,
                                  @Nonnull final OurRoutes webResource) {
        this.clazz = clazz;
        this.webResource = webResource;
    }

    public void add(@Nonnull T... objs) {
        this.objects.addAll(Arrays.asList(objs));
    }

    @Nonnull
    public String route() {
        return webResource.getPath();
    }

    @Nonnull
    public List<T> getObjects() {
        return objects;
    }

    @Nonnull
    public Class<T> getClazz() {
        return clazz;
    }
}
