package vsp.adventurer_api.entities;

public class GroupWrapper {

    private Group object;

    private String status;

    public GroupWrapper(final Group object, final String status) {
        this.object = object;
        this.status = status;
    }

    public Group getObject() {
        return object;
    }

    public void setObject(final Group object) {
        this.object = object;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }
}
