package vsp.adventurer_api.entities.group;

import java.util.List;

public class CreatedGroup {

    private String message;

    private List<Group> object;

    private String status;

    public CreatedGroup(String message, List<Group> object, String status) {
        this.message = message;
        this.object = object;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Group> getObject() {
        return object;
    }

    public Group getGroup() {
        return object.get(0); // danger !!!
    }

    public void setObject(List<Group> object) {
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
        return "CreatedGroup{" +
                "message='" + message + '\'' +
                ", object=" + object +
                ", status='" + status + '\'' +
                '}';
    }
}
