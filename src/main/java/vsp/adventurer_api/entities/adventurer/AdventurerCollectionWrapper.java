package vsp.adventurer_api.entities.adventurer;

import java.util.List;

public class AdventurerCollectionWrapper {

    private List<Adventurer> objects;

    private String status;

    public AdventurerCollectionWrapper(final List<Adventurer> objects, final String status) {
        this.objects = objects;
        this.status = status;
    }

    public List<Adventurer> getObjects() {
        return objects;
    }

    public void setObjects(final List<Adventurer> objects) {
        this.objects = objects;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }
}
