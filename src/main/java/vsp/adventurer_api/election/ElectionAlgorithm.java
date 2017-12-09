package vsp.adventurer_api.election;

public interface ElectionAlgorithm {

    /**
     * Joins a topology and receives a ID.
     *
     * @param participant will join the topology.
     */
    ElectionParticipant join(ElectionParticipant participant);

    /**
     * Returns true if this server is the coordinator.
     *
     * @return True if this server is the coordinator.
     */
    boolean isCoordinator();

    /**
     * @return The coordinator/bully
     */
    ElectionParticipant getCoordinator();

    /**
     * Returns true when a election is in process.
     *
     * @return True when a election is in process.
     */
    boolean isProcessing();

    /**
     * Starts a election.
     */
    void election();

    /**
     * Confirms the receipt of a 'election'-message.
     */
    void answer();

    /**
     * Information that this server is the new coordinator.
     */
    void coordinator();

}
