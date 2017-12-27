package vsp.adventurer_api.election;

import vsp.adventurer_api.entities.assignment.Assignment;

public class ElectionMessage {

    /**
     * name of the algorithm used
     */
    private String algorithm;

    /**
     * the payload for the current state of the algorithm
     */
    private String payload;

    /**
     * uri of the user sending this request
     */
    private String user;

    /**
     * JSON description of the job to do
     */
    private Assignment job;

    /**
     * something you want to tell the other one
     */
    private String message;

    public ElectionMessage(final String algorithm, final String payload, final String user, final Assignment job, final String message) {
        this.algorithm = algorithm;
        this.payload = payload;
        this.user = user;
        this.job = job;
        this.message = message;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(final String algorithm) {
        this.algorithm = algorithm;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(final String payload) {
        this.payload = payload;
    }

    public String getUser() {
        return user;
    }

    public void setUser(final String user) {
        this.user = user;
    }

    public Assignment getJob() {
        return job;
    }

    public void setJob(final Assignment job) {
        this.job = job;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Election{" +
                "algorithm='" + algorithm + '\'' +
                ", payload='" + payload + '\'' +
                ", user='" + user + '\'' +
                ", job=" + job +
                ", message='" + message + '\'' +
                '}';
    }
}
