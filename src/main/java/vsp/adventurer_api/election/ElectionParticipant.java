package vsp.adventurer_api.election;

import org.apache.log4j.Logger;
import vsp.adventurer_api.http.api.OurRoutes;

import javax.annotation.Nullable;

public class ElectionParticipant implements Cloneable {
    private final static Logger LOG = Logger.getLogger(ElectionParticipant.class);

    @Nullable
    private Long id;

    /**
     * IP to the election route.
     */
    private String ip;

    private int port;

    private String route;

    public ElectionParticipant(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.route = OurRoutes.ELECTION.getPath();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getElectionRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getURL() {
        return String.format("%s:%d%s", ip, port, route);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElectionParticipant that = (ElectionParticipant) o;

        if (port != that.port) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
        return route != null ? route.equals(that.route) : that.route == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (ip != null ? ip.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (route != null ? route.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ElectionParticipant{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", route='" + route + '\'' +
                '}';
    }

    @Override
    protected ElectionParticipant clone() {
        try {
            return (ElectionParticipant) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

}
