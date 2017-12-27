package vsp.adventurer_api.mutex;

public class MutexStateMessage {

    /**
     * current state: released, wanting, held
     */
    private String state;

    /**
     * int, the current lamport clock
     */
    private int time;

    public MutexStateMessage(String state, int time) {
        this.state = state;
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}