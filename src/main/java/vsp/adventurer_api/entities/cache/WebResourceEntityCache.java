package vsp.adventurer_api.entities.cache;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Holds all objects that should be visible as resource. <b>No DB needed because this application runs in a container.</b>
 */
public class WebResourceEntityCache<T> {

    @Nonnull
    private String webResource;

    @Nonnull
    private final List<T> objects = new ArrayList<>();

    @Nonnull
    private final Class<T> clazz;

    public WebResourceEntityCache(@Nonnull final Class<T> clazz,
                                  @Nonnull final String webResource) {
        this.clazz = clazz;
        this.webResource = webResource;
    }

    public void add(@Nonnull T... objs) {
        this.objects.addAll(Arrays.asList(objs));
    }

    @Nonnull
    public String route() {
        return webResource;
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
