package vsp.adventurer_api.utility;

import com.google.common.primitives.Ints;
import org.apache.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URL {
    private final static Logger LOG = Logger.getLogger(URL.class);

    @Nonnull
    private final String address;

    @Nonnull
    private final Integer port;

    @Nullable
    private String route;


    public URL(@Nonnull String address, @Nonnull Integer port) {
        this.address = address;
        this.port = port;
    }

    public URL(@Nonnull String address, @Nonnull Integer port, @Nullable String route) {
        this.address = address;
        this.port = port;
        this.route = route;
    }

    /**
     * Splits "1.2.3.4:1234/route" into ["1.2.3.4","1234","/route]. Port ("1234") will parsable to integer.
     *
     * @throws IllegalArgumentException If given param is no valid url.
     */
    public static URL parse(String url) {
        LOG.debug(">>> Parsing: " + url);

        final String protocol = "((?<protocol>https?)://)?";
        final String ip = "(?<ip>[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3})";
        final String port = ":(?<port>[0-9]{1,6})?";
        final String route = "(?<route>/.*)?";
        final Pattern urlPattern = Pattern.compile(protocol + ip + port + route);

        Matcher matcher = urlPattern.matcher(url);

        if (!matcher.matches())
            throw new IllegalArgumentException("no valid url");
        if (Ints.tryParse(matcher.group("port")) == null)
            throw new IllegalArgumentException("url has no valid port");

        final String routeGroup = matcher.group("route");
        return routeGroup == null || "/".equals(routeGroup) ?
                new URL(matcher.group("ip"), Integer.parseInt(matcher.group("port"))) :
                new URL(matcher.group("ip"),
                        Integer.parseInt(matcher.group("port")),
                        routeGroup);
    }

    public boolean isWithRoute() {
        return route != null;
    }

    @Nonnull
    public String getAddress() {
        return address;
    }

    @Nonnull
    public Integer getPort() {
        return port;
    }

    @Nullable
    public String getRoute() {
        return route;
    }

    public void setRoute(@Nullable String route) {
        this.route = route;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        URL url = (URL) o;

        if (!address.equals(url.address)) return false;
        if (!port.equals(url.port)) return false;
        return route != null ? route.equals(url.route) : url.route == null;
    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + port.hashCode();
        result = 31 * result + (route != null ? route.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return address + ":" + port + (route != null ? route : "");
    }

    public static boolean isValid(final String url1) {
        try {
            parse(url1);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean equals(final String url1, final String url2) {
        return parse(url1).equals(parse(url2));
    }
}
