package vsp.adventurer_api.entities;

public class Adventurer {

    private String capabilities;

    private String heroclass;

    private String url;

    private String user;

    public Adventurer(String capabilities, String heroclass, String url, String user) {
        this.capabilities = capabilities;
        this.heroclass = heroclass;
        this.url = url;
        this.user = user;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getHeroclass() {
        return heroclass;
    }

    public void setHeroclass(String heroclass) {
        this.heroclass = heroclass;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Adventurer{" +
                "capabilities='" + capabilities + '\'' +
                ", heroclass='" + heroclass + '\'' +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                '}';
    }
}
