package vsp.adventurer_api.entities.basic;

import vsp.adventurer_api.http.api.OurRoutes;

public class ServiceEndpoint {

    /**
     * link to the registered user account
     */
    private String user;

    /**
     * if you have no assignment currently
     */
    private boolean idle;

    /**
     * url to the group you are in
     */
    private String group;

    /**
     * route to which one may post to hire you for a group
     */
    private String hirings;

    /**
     * route to which one may post an assignment
     */
    private String assignments;

    /**
     * route to which one may post messages
     */
    private String messages;

    /**
     * uri to which one sends election messages to
     */
    private String election;

    public ServiceEndpoint(String user, boolean idle, String group, String hirings, String assignments, String messages, String election) {
        this.user = user;
        this.idle = idle;
        this.group = group;
        this.hirings = hirings;
        this.assignments = assignments;
        this.messages = messages;
        this.election = election;
    }

    public ServiceEndpoint(String user, boolean idle) {
        this.user = user;
        this.idle = idle;
        this.group = OurRoutes.GROUP.getPath();
        this.hirings = OurRoutes.HIRINGS.getPath();
        this.assignments = OurRoutes.ASSIGNMENTS.getPath();
        this.messages = OurRoutes.MESSAGES.getPath();
        this.election = OurRoutes.ELECTION.getPath();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isIdle() {
        return idle;
    }

    public void setIdle(boolean idle) {
        this.idle = idle;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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

    public String getElection() {
        return election;
    }
}