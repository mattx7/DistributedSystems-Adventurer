package vsp.adventurer_api.entities.basic;

import vsp.adventurer_api.http.api.OurRoutes;

public class ServiceEndpoint {

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
     * route to which one may post to hire you for a group
     */
    private final String hirings;

    /**
     * route to which one may post an assignment
     */
    private final String assignments;

    /**
     * route to which one may post messages
     */
    private final String messages;

    public ServiceEndpoint(String user, boolean idle, String group, String hirings, String assignments, String messages) {
        this.user = user;
        this.idle = idle;
        this.group = group;
        this.hirings = hirings;
        this.assignments = assignments;
        this.messages = messages;
    }

    public ServiceEndpoint(String user, boolean idle) {
        this.user = user;
        this.idle = idle;
        this.group = OurRoutes.GROUP.getPath();
        this.hirings = OurRoutes.HIRINGS.getPath();
        this.assignments = OurRoutes.ASSIGNMENTS.getPath();
        this.messages = OurRoutes.MESSAGES.getPath();
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