package vsp.adventurer_api.entities;

import java.util.List;

public class Group {

    private int id;

    private List<String> member;

    private String owner;

    public Group(final int id, final List<String> member, final String owner) {
        this.id = id;
        this.member = member;
        this.owner = owner;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public List<String> getMember() {
        return member;
    }

    public void setMember(final List<String> member) {
        this.member = member;
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
                ", member=" + member +
                ", owner='" + owner + '\'' +
                '}';
    }
}
