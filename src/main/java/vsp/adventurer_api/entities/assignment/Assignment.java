package vsp.adventurer_api.entities.assignment;

/**
 * To fulfill the quests the owner of the group can assign tasks to the other members of the
 * group by posting to the corresponding hero service at the “assignments” url
 */
public class Assignment {

    /**
     * some identity chosen by the initiator to identify this request
     */
    private String id;

    /**
     * uri to the task to accomplish
     */
    private String task;

    /**
     * uri or url to resource where actions are required
     */
    private String resource;

    /**
     * method to take – if already known
     */
    private String method;

    /**
     * data to use/post for the task
     */
    private String data;

    /**
     * an url where the initiator can be reached with the results/token
     */
    private String callback;

    /**
     * something you want to tell the other one
     */
    private String message;

    public Assignment(final String id, final String task, final String resource, final String method, final String data, final String callback, final String message) {
        this.id = id;
        this.task = task;
        this.resource = resource;
        this.method = method;
        this.data = data;
        this.callback = callback;
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

    public String getCallback() {
        return callback;
    }

    public void setCallback(final String callback) {
        this.callback = callback;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "id='" + id + '\'' +
                ", task='" + task + '\'' +
                ", resource='" + resource + '\'' +
                ", method='" + method + '\'' +
                ", data='" + data + '\'' +
                ", callback='" + callback + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}