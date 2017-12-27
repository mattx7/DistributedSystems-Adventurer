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

    /**
     * uri to endpoint where one posts mutex algorithm messages
     */
    private String mutex;

    /**
     * uri to endpoint telling the mutex state
     */
    private String mutexstate;

    public ServiceEndpoint(String user, boolean idle) {
        this.user = user;
        this.idle = idle;
        this.group = OurRoutes.GROUP;
        this.hirings = OurRoutes.HIRINGS;
        this.assignments = OurRoutes.ASSIGNMENTS;
        this.messages = OurRoutes.MESSAGES;
        this.election = OurRoutes.ELECTION;
        this.mutex = OurRoutes.MUTEX;
        this.mutexstate = OurRoutes.MUTEX_STATE;
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

    public String getMutex() {
        return mutex;
    }

    public void setMutex(String mutex) {
        this.mutex = mutex;
    }

    public String getMutexstate() {
        return mutexstate;
    }

    public void setMutexstate(String mutexstate) {
        this.mutexstate = mutexstate;
    }
}