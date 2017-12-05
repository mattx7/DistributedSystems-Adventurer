package vsp.adventurer_api.entities.group;

import java.util.List;

public class Group {

    private int id;

    private List<String> members;

    private String owner;

    public Group(final int id, final List<String> members, final String owner) {
        this.id = id;
        this.members = members;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(final List<String> members) {
        this.members = members;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(final String owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id=" + id +
                ", members=" + members +
                ", owner='" + owner + '\'' +
                '}';
    }
}
