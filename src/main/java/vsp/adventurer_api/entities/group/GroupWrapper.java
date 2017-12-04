package vsp.adventurer_api.entities.group;

public class GroupWrapper {

    private GroupWrapperLinks _link;

    private Group object;

    private String status;

    public GroupWrapper(final GroupWrapperLinks _link, final Group object, final String status) {
        this._link = _link;
        this.object = object;
        this.status = status;
    }

    public GroupWrapperLinks get_link() {
        return _link;
    }

    public void set_link(final GroupWrapperLinks _link) {
        this._link = _link;
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

    @Override
    public String toString() {
        return "GroupWrapper{" +
                "_link=" + _link +
                ", object=" + object +
                ", status='" + status + '\'' +
                '}';
    }
}
