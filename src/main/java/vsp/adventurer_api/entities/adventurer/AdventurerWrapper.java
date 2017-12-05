package vsp.adventurer_api.entities.adventurer;

public class AdventurerWrapper {

    private Adventurer object;

    private String status;

    public AdventurerWrapper(Adventurer object, String status) {
        this.object = object;
        this.status = status;
    }

    public Adventurer getObject() {
        return object;
    }

    public void setObject(Adventurer object) {
        this.object = object;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AdventurerWrapper{" +
                "object=" + object +
                ", status='" + status + '\'' +
                '}';
    }
}
