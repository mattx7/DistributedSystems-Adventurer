package vsp.adventurer_api.entities;

/**
 * To fulfill the quests the owner of the group can assign tasks to the other members of the
 * group by posting to the corresponding hero service at the “assignments” url
 */
public class Assignment {
    private static long idCounter = 0;

    /**
     * some identity chosen by the initiator to identify this reques
     */
    private String id;

    /**
     * uri to the task to accomplish
     */
    private String task;

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


    public Assignment(String id, String task, String method, String data, String callback, String message) {
        this.id = id;
        this.task = task;
        this.method = method;
        this.data = data;
        this.callback = callback;
        this.message = message;
    }

    public static long getIdCounter() {
        return idCounter;
    }

    public static void setIdCounter(long idCounter) {
        Assignment.idCounter = idCounter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCallback() {
        return callback;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}