package vsp.adventurer_api.entities;

public class CreateAdventurer {

    private final String heroclass;

    private final String capabilities;

    private final String url;

    public CreateAdventurer(String heroclass, String capabilities, String url) {
        this.heroclass = heroclass;
        this.capabilities = capabilities;
        this.url = url;
    }

    public String getHeroclass() {
        return heroclass;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public String getUrl() {
        return url;
    }
}
