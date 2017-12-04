package vsp.adventurer_api.entities;

public class TaskResult {

    /**
     * the identity chosen by the initiator for the request
     */
    private String id;

    /**
     * same as assignment
     */
    private String task;

    /**
     * same as assignment
     */
    private String resource;

    /**
     * method used to get this result
     */
    private String method;

    /**
     * the whole response data/result of the action
     */
    private String data;

    /**
     * uri to the user solved the task (own account at the blackboard
     */
    private String user;

    /**
     * <something you want to tell the other one
     */
    private String message;

    public TaskResult(final String id, final String task, final String resource, final String method, final String data, final String user, final String message) {
        this.id = id;
        this.task = task;
        this.resource = resource;
        this.method = method;
        this.data = data;
        this.user = user;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(final String task) {
        this.task = task;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(final String resource) {
        this.resource = resource;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(final String method) {
        this.method = method;
    }

    public String getData() {
        return data;
    }

    public void setData(final String data) {
        this.data = data;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }
}
