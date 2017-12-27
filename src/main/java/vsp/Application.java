package vsp;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import vsp.adventurer_api.APIClient;
import vsp.adventurer_api.cache.Cache;
import vsp.adventurer_api.election.BullyAlgorithm;
import vsp.adventurer_api.election.ElectionParticipant;
import vsp.adventurer_api.entities.Message;
import vsp.adventurer_api.entities.adventurer.Adventurer;
import vsp.adventurer_api.entities.adventurer.AdventurerCollectionWrapper;
import vsp.adventurer_api.entities.adventurer.AdventurerWrapper;
import vsp.adventurer_api.entities.adventurer.CreateAdventurer;
import vsp.adventurer_api.entities.assignment.Assignment;
import vsp.adventurer_api.entities.basic.User;
import vsp.adventurer_api.entities.group.Group;
import vsp.adventurer_api.entities.group.GroupWrapper;
import vsp.adventurer_api.http.api.BlackboardRoutes;
import vsp.adventurer_api.http.api.OurRoutes;
import vsp.adventurer_api.mutex.MutexAlgorithm;
import vsp.adventurer_api.mutex.MutexMessage;
import vsp.adventurer_api.terminal.CommandPrompt;
import vsp.adventurer_api.utility.URL;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

/**
 * Runs application and interactions.
 */
public class Application {
    private static Logger LOG = Logger.getLogger(Application.class);

    public static final int BLACKBOARD_PORT = 24000;
    public static final int OWN_PORT = 4567;

    @Nonnull
    private static final Gson CONVERTER = new Gson();


    /**
     * Will be set after start.
     */
    @Nullable
    public static String ownIp;

    /**
     * Holds client for everyone
     */
    public static APIClient client;

    /**
     * Our adventurer in the taverna.
     */
    public static CreateAdventurer adventurer;

    /**
     * Our user.
     */
    public static User user;

    /**
     * The election algorithm.
     */
    public static BullyAlgorithm election = new BullyAlgorithm();

    public static MutexAlgorithm mutexAlgorithm = new MutexAlgorithm();

    public static CommandPrompt commandPrompt = new CommandPrompt();

    /**
     * Holds only the main method an instance is not necessary.
     */
    private Application() {
    }

    public static void main(String[] args) {
        LOG.info("Starting application...");
        commandPrompt.run();
    }

    /**
     * Gets a adventurer from the taverna by name.
     */
    public static Adventurer getAdventurer(@Nonnull APIClient client, String name) throws IOException {
        final AdventurerWrapper wrapper = client.get(user, BlackboardRoutes.ADVENTURERS + "/" + name).getAs(AdventurerWrapper.class);
        return wrapper.getObject();
    }

    /**
     * Gets a adventurer from the taverna by name.
     */
    public static List<Adventurer> getAdventurers() throws IOException {
        final AdventurerCollectionWrapper wrapper = client.get(user, BlackboardRoutes.ADVENTURERS).getAs(AdventurerCollectionWrapper.class);
        return wrapper.getObjects();
    }

    public static void sleep() {
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            LOG.error("Sleep interrupted: ", e);
        }
    }

    /**
     * Creates/Updates our adventurer in the tavern.
     *
     * @see #adventurer
     */
    public static void postAdventurer(User user) throws IOException {
        print(client.post(
                user,
                BlackboardRoutes.ADVENTURERS,
                CONVERTER.toJson(adventurer)).getJson());
    }

    /**
     * Adds new capabilities to the adventurer-service.
     *
     * @param capability Not null
     * @throws IOException
     */
    public static void addCapabilities(@Nonnull String... capability) throws IOException {
        Arrays.asList(capability).forEach(e -> adventurer.addCapabilities(e));
        postAdventurer(user);
    }

    public static void updateGroupMembers(@Nonnull APIClient client, @Nonnull User user) throws IOException {
        for (final Group group1 : Cache.GROUPS.getObjects()) {
            group1.setMembers(client.get(user, BlackboardRoutes.GROUP + "/" + group1.getId()).getAs(GroupWrapper.class).getObject().getMembers());
        }
    }


    public static void print(@Nonnull String message) {
        System.out.println(message);
    }


    public static void handleNewAssignment(Assignment assignment) {
        Cache.ASSIGNMENTS.add(assignment);
        print(">>> New Assignment: " + assignment);
        // sleep();
        // FacadeController.SINGLETON.updateAssignments();
    }

    @Nonnull
    public static Set<ElectionParticipant> sendElection(Collection<ElectionParticipant> possibleCoordinator) {
        Set<ElectionParticipant> offlineParticipants = new HashSet<>();

        for (ElectionParticipant participant : possibleCoordinator) {
            client.setTargetURL(participant.getIp(), participant.getPort());
            LOG.info("Sending election to " + participant);
            try {
                Message message = client.post(user, participant.getElectionRoute(), CONVERTER.toJson(election.getYourself())).getAs(Message.class);
                print(message != null ? participant.getIp() + ": " + message.getMessage() : "");
            } catch (IOException e) {
                print(participant.getIp() + " is offline!!!");
                offlineParticipants.add(participant);
            }
            client.backToOldTarget();
        }
        return offlineParticipants;
    }

    public static void sendCoordinator(ElectionParticipant yourself) {
        try {
            for (ElectionParticipant participant : election.getParticipants()) {
                client.setTargetURL(participant.getIp(), participant.getPort());
                print(client.post(user, OurRoutes.COORDINATOR, CONVERTER.toJson(yourself)).getJson());
                client.backToOldTarget();
            }
        } catch (IOException e) {
            LOG.error(e);
        }
    }

    public static void sendMutexMsg(URL url, MutexMessage mutexMessage) throws IOException {
        try {
            client.setTargetURL(url.getAddress(), url.getPort());
            client.post(user, OurRoutes.MUTEX, CONVERTER.toJson(mutexMessage));
        } finally {
            client.backToOldTarget();
        }
    }

}