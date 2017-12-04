package vsp.adventurer_api.entities.group;

public class GroupWrapperLinks {

    private String members;

    private String self;

    public GroupWrapperLinks(final String members, final String self) {
        this.members = members;
        this.self = self;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(final String members) {
        this.members = members;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(final String self) {
        this.self = self;
    }

    @Override
    public String toString() {
        return "GroupWrapperLinks{" +
                "members='" + members + '\'' +
                ", self='" + self + '\'' +
                '}';
    }
}
