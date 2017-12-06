package vsp.adventurer_api.entities.adventurer;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

public class CreateAdventurer {
    private final static Logger LOG = Logger.getLogger(CreateAdventurer.class);

    private final String heroclass;

    private String capabilities;

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

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public void addCapabilities(String capability) {
        List<String> capabilityList = Arrays.asList(getCapabilities().split(","));
        capabilityList.add(capability);
        this.capabilities = StringUtils.join(capabilityList, ",");
        LOG.debug("New Capabilities: " + this.capabilities);
    }

    public String getUrl() {
        return url;
    }
}
