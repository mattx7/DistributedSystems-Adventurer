package vsp.adventurer_api;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import vsp.Application;
import vsp.adventurer_api.election.ElectionParticipant;
import vsp.adventurer_api.entities.Message;
import vsp.adventurer_api.entities.adventurer.AdventurerWrapper;
import vsp.adventurer_api.entities.assignment.Assignment;
import vsp.adventurer_api.entities.assignment.TaskResult;
import vsp.adventurer_api.entities.basic.ServiceEndpoint;
import vsp.adventurer_api.entities.basic.User;
import vsp.adventurer_api.entities.cache.Cache;
import vsp.adventurer_api.entities.group.Group;
import vsp.adventurer_api.entities.group.GroupWrapper;
import vsp.adventurer_api.entities.group.Hiring;
import vsp.adventurer_api.http.HTTPResponse;
import vsp.adventurer_api.http.api.BlackboardRoutes;
import vsp.adventurer_api.http.api.OurRoutes;
import vsp.adventurer_api.http.api.Route;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import static spark.Spark.*;

public enum ServiceController {
    SINGLETON;

    private static final Logger LOG = Logger.getLogger(ServiceController.class);

    /**
     * JSON converter
     */
    private final Gson converter = new Gson();

    /**
     * Holds all possible routes of this service.
     */
    private ServiceEndpoint endpoint = new ServiceEndpoint("", false);

    /**
     * Starts the service.
     */
    public void run(User user) {
        final String userURI = Route.concat(BlackboardRoutes.USERS, user.getName());
        endpoint.setUser(userURI);

        // basic information
        get("/", (req, resp) -> converter.toJson(endpoint));

        // our adventurer
        get(userURI, (req, resp) -> converter.toJson(user));

        post(Cache.HIRINGS.route(), (req, resp) -> {
            LOG.debug("reqBody: " + req.body());
            final Hiring hiring;
            try {
                hiring = converter.fromJson(req.body(), Hiring.class);
                final boolean isAccepted = Application.acceptToNewHiring(req.body());
                if (isAccepted) {
                    Application.CLIENT.setDefaultURL(); // to blackboard in case of sending hiring to self
                    String groupToJoinRoute = hiring.getGroup();

                    final Group joinedGroup = joinGroup(user, groupToJoinRoute);

                    Application.addCapabilities(user, "group", "election");

                    // get election bully
                    String owner = joinedGroup.getOwner();
                    AdventurerWrapper ownersAdventurer = Application.CLIENT
                            .get(user, BlackboardRoutes.ADVENTURERS + "/" + owner)
                            .getAs(AdventurerWrapper.class);

                    Application.CLIENT.backToOldTarget();

                    joinTopology(user, ownersAdventurer);
                    resp.status(200);
                    return converter.toJson(new Message("hiring accepted"));
                } else {
                    return halt(406, converter.toJson(new Message("hiring rejected")));
                }
            } catch (JsonSyntaxException e) {
                return halt(401, "JsonSyntaxException");
            }
        });

        // react to post on /assignments
        post(Cache.ASSIGNMENTS.route(), (req, resp) -> {
            LOG.info("reqBody: " + req.body());

            if (endpoint.isIdle())
                return halt(406, "Does already have an assignment");

            final Assignment assignment;
            try {
                assignment = converter.fromJson(req.body(), Assignment.class);
                getEndpoint().setIdle(true);
                Application.handleNewAssignment(assignment);
                resp.status(200);
                return resp;
            } catch (JsonSyntaxException e) {
                return halt(401, "JsonSyntaxException");
            }

        });

        post(OurRoutes.RESULTS, (req, resp) -> {
            final TaskResult result = converter.fromJson(req.body(), TaskResult.class);
            LOG.info("received json: \n" + result);
            getEndpoint().setIdle(false);
            Cache.RESULTS.add(result);
            Cache.ASSIGNMENTS.getObjects().clear();
            return resp;
        });

        post(Cache.ELECTIONS.route(), (req, resp) -> {
            ElectionParticipant participant = converter.fromJson(req.body(), ElectionParticipant.class);
            LOG.info(">>> ELECTION REQUEST FROM: \n" + participant);
            Application.election.process();

            resp.status(200);
            return converter.toJson(new Message("OK"));
        });

        post(OurRoutes.GROUP, (req, resp) -> {
            ElectionParticipant[] participants = converter.fromJson(req.body(), ElectionParticipant[].class);

            System.out.println(">>> Participants: ");
            Arrays.asList(participants).forEach(e -> System.out.print(e.getURL() + ", "));
            System.out.println("");

            Application.election.clear();
            Application.election.add(participants);

            resp.status(200);
            return resp;
        });

        post(OurRoutes.JOIN, (req, resp) -> {
            final ElectionParticipant newParticipant = converter.fromJson(req.body(), ElectionParticipant.class);
            final ElectionParticipant join = Application.election.join(newParticipant);// gives the participant a new id
            LOG.info(">>> NEW PARTICIPANT: " + join);

            informEveryoneAboutTheNewParticipant(user);

            resp.status(200);
            return converter.toJson(join);
        });

        post(OurRoutes.COORDINATOR, (req, resp) -> {
            String body = req.body();
            LOG.info(">>> COORDINATOR :  " + body);
            ElectionParticipant coordinator = converter.fromJson(body, ElectionParticipant.class);
            Application.election.setCoordinator(coordinator);
            resp.status(200);
            return resp;
        });
    }


    // ==========================================================

    /**
     * Joining the topology by the url of the adventurer.
     */
    private void joinTopology(User user, AdventurerWrapper ownersAdventurer) throws IOException {
        String ownersAddress = ownersAdventurer.getObject().getUrl().split(":")[0];
        Application.CLIENT.setTargetURL(ownersAddress, 4567);
        LOG.info("Looking for owner " + ownersAddress);
        // join - returns yourself with given id
        ElectionParticipant yourself = Application.CLIENT.post(user, OurRoutes.JOIN,
                converter.toJson(new ElectionParticipant(Application.OWN_IP, Application.OWN_PORT)))
                .getAs(ElectionParticipant.class);
        // Application.election.add(yourself); already added because join sends a "broadcast" with participants
        Application.election.setYourself(yourself);

        Application.CLIENT.backToOldTarget();
    }

    @Nonnull
    private Group joinGroup(User user, String groupToJoinRoute) throws IOException {
        final HTTPResponse response = Application.CLIENT.post(user, groupToJoinRoute + "/members", "");
        getEndpoint().setGroup(Application.CLIENT.getDefaultURL().split("//")[1] + groupToJoinRoute);
        LOG.info("received json: \n" + response.getJson());

        // add group
        GroupWrapper groupWrapper = Application.CLIENT.get(user, groupToJoinRoute).getAs(GroupWrapper.class);
        Cache.GROUPS.getObjects().clear();
        Cache.GROUPS.add(groupWrapper.getObject());
        return groupWrapper.getObject();
    }

    public ServiceEndpoint getEndpoint() {
        return endpoint;
    }

    private void informEveryoneAboutTheNewParticipant(User user) throws IOException {
        Set<ElectionParticipant> participants = Application.election.getParticipants();
        ElectionParticipant[] participantsArray = participants.toArray(new ElectionParticipant[participants.size()]);
        LOG.info("ARRAY: " + Arrays.toString(participantsArray));
        String participantsJSON = converter.toJson(participantsArray);

        for (ElectionParticipant next : participants) {
            Application.CLIENT.setTargetURL(next.getIp(), next.getPort());

            LOG.info("Sending all participants to " + next.getURL() + ":" + next.getPort());
            LOG.info("JSON:\n" + participantsJSON);

            Application.CLIENT.post(user,
                    OurRoutes.GROUP,
                    participantsJSON);

            Application.CLIENT.setDefaultURL();
        }
    }

}