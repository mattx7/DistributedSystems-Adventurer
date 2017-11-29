package vsp.adventurer_api.entities;

public class Adventurer {

    /**
     * link to the registered user account
     */
    private final String user;

    /**
     * if you have no assignment currently
     */
    private final boolean idle;

    /**
     * url to the group you are in
     */
    private final String group;

    /**
     * uri to which one may post to hire you for a group
     */
    private final String hirings;

    /**
     * uri to which one may post an assignment
     */
    private final String assignments;

    /**
     * uri to which one may post messages
     */
    private final String messages;

    public Adventurer(String user, boolean idle, String group, String hirings, String assignments, String messages) {
        this.user = user;
        this.idle = idle;
        this.group = group;
        this.hirings = hirings;
        this.assignments = assignments;
        this.messages = messages;
    }

    public String getUser() {
        return user;
    }

    public boolean isIdle() {
        return idle;
    }

    public String getGroup() {
        return group;
    }

    public String getHirings() {
        return hirings;
    }

    public String getAssignments() {
        return assignments;
    }

    public String getMessages() {
        return messages;
    }
}