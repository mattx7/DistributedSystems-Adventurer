package vsp.adventurer_api.election;

import org.apache.log4j.Logger;
import vsp.Application;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class BullyAlgorithm {
    private static final Logger LOG = Logger.getLogger(BullyAlgorithm.class);

    /**
     * Should be increase after access.
     */
    private static int idCount = 1;

    /**
     * All registered participants.
     */
    private Set<ElectionParticipant> participants = new HashSet<>();

    /**
     * Holds the coordinator/bully.
     */
    private ElectionParticipant coordinator;

    /**
     * Yourself as participant.
     */
    private ElectionParticipant yourself;

    private boolean isInProgress = false;

    public BullyAlgorithm() {
    }

    /**
     * Returns id and increases the counter afterwards.
     */
    long nextId() {
        return idCount++;
    }

    /**
     * Given participants joins the topology.
     *
     * @param newParticipant Not null.
     * @return Given participant with a new ID.
     */
    public ElectionParticipant join(@Nonnull ElectionParticipant newParticipant) {
        if (this.participants.add(newParticipant)) {
            newParticipant.setId(nextId());
            return newParticipant;
        }
        throw new IllegalArgumentException("Participant is already in the topology");
    }

    /**
     * Registers a already existing participants to the topology on this server.
     *
     * @param alreadyJoinedParticipants has to already a part of the topology.
     */
    public void add(@Nonnull ElectionParticipant... alreadyJoinedParticipants) {
        Arrays.asList(alreadyJoinedParticipants).forEach(elem -> {
            System.out.println("adding " + elem);
            Long newID = elem.getId();
            checkArgument(newID != null);
            if (idCount < newID) {
                idCount = Math.toIntExact(newID);
            }
            this.participants.add(elem);

        });
    }


    public boolean isCoordinator() {
        return yourself.equals(coordinator);
    }

    public ElectionParticipant getCoordinator() {
        return coordinator;
    }

    public boolean isProcessing() {
        return isInProgress;
    }

    public void clear() {
        participants.clear();
    }

    @Nonnull
    public ElectionParticipant getYourself() {
        return yourself;
    }

    public void setCoordinator(ElectionParticipant coordinator) {
        isInProgress = false;
        this.coordinator = coordinator;
    }

    public void setYourself(@Nonnull ElectionParticipant yourself) {
        this.yourself = yourself;
    }

    public Set<ElectionParticipant> getParticipants() {
        return participants.stream().map(ElectionParticipant::clone).collect(Collectors.toSet());
    }

    public void process() {
        if (isProcessing())
            return;

        isInProgress = true;

        LOG.info(">>> Yourself: " + yourself);

        Set<ElectionParticipant> possibleCoordinators = participants.stream()
                .filter(e -> (e.getId() < yourself.getId()))
                .collect(Collectors.toSet());

        LOG.info(">>> Possible coordinators: " + possibleCoordinators);

        if (possibleCoordinators.isEmpty()) {
            ElectionParticipant yourself = Application.election.getYourself();
            setCoordinator(yourself);
            Application.sendCoordinator(yourself);
            return;
        }

        Set<ElectionParticipant> offlineParticipants = Application.sendElection(possibleCoordinators);
        participants.removeAll(offlineParticipants);

        if (offlineParticipants.equals(possibleCoordinators)) {
            Application.sendCoordinator(yourself);
        }
    }

}
