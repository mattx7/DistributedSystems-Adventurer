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
import vsp.adventurer_api.entities.group.GroupWrapper;
import vsp.adventurer_api.entities.group.Hiring;
import vsp.adventurer_api.http.HTTPResponse;
import vsp.adventurer_api.http.api.BlackboardRoutes;
import vsp.adventurer_api.http.api.OurRoutes;

import java.util.Arrays;
import java.util.Set;

import static spark.Spark.*;

public enum FacadeController {
    SINGLETON;

    private static final Logger LOG = Logger.getLogger(FacadeController.class);

    private final Gson converter = new Gson();
    private ServiceEndpoint endpoint = new ServiceEndpoint("", false);

    public void run(User user, String userURI) {
        endpoint.setUser(userURI);
        // basic information
        get("/", (req, resp) -> converter.toJson(endpoint));

        // our adventurer
        get(userURI, (req, resp) -> converter.toJson(user));

        // basic routes
        //Lists.<WebResourceEntityCache>newArrayList(Cache.GROUPS, Cache.HIRINGS, Cache.ASSIGNMENTS, Cache.MESSAGES)
        //        .forEach(webResource -> get(
        //                webResource.route(), (req, resp) -> converter.toJson(webResource.getObjects())));

        // react to post on /hirings
        post(Cache.HIRINGS.route(), (req, resp) -> {
            LOG.debug("reqBody: " + req.body());
            final Hiring hiring;
            try {
                hiring = converter.fromJson(req.body(), Hiring.class);
                final boolean isAccepted = Application.acceptToNewHiring(req.body());
                if (isAccepted) {
                    Application.client.setDefaultURL(); // to blackboard in case of sending hiring to self

                    String groupRoute = hiring.getGroup();
                    getEndpoint().setGroup(Application.client.getDefaultURL().split("//")[1] + groupRoute);
                    final HTTPResponse response = Application.client.post(user, groupRoute + "/members", "");
                    LOG.info("received json: \n" + response.getJson());

                    // add group
                    GroupWrapper groupWrapper = Application.client.get(user, groupRoute).getAs(GroupWrapper.class);
                    Cache.GROUPS.getObjects().clear();
                    Cache.GROUPS.add(groupWrapper.getObject());

                    Application.adventurer.addCapabilities("group");
                    Application.adventurer.addCapabilities("election");
                    Application.postAdventurer(user);

                    // get election bully
                    String owner = groupWrapper.getObject().getOwner();
                    AdventurerWrapper ownersAdventurer = Application.client
                            .get(user, BlackboardRoutes.ADVENTURERS.getPath() + "/" + owner)
                            .getAs(AdventurerWrapper.class);

                    Application.client.backToOldTarget();

                    String ownersAddress = ownersAdventurer.getObject().getUrl().split(":")[0];
                    Application.client.setTargetURL(ownersAddress, 4567);
                    LOG.info("Looking for owner " + ownersAddress);
                    // join - returns yourself with given id
                    ElectionParticipant yourself = Application.client.post(user, OurRoutes.JOIN.getPath(),
                            converter.toJson(new ElectionParticipant(Application.OWN_IP, Application.OWN_PORT)))
                            .getAs(ElectionParticipant.class);
                    // Application.election.add(yourself); already added because join sends a "broadcast" with participants
                    Application.election.setYourself(yourself);

                    Application.client.backToOldTarget();
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

        post(OurRoutes.RESULTS.getPath(), (req, resp) -> {
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

        post(OurRoutes.GROUP.getPath(), (req, resp) -> {
            ElectionParticipant[] participants = converter.fromJson(req.body(), ElectionParticipant[].class);

            System.out.println(">>> Participants: ");
            Arrays.asList(participants).forEach(e -> System.out.print(e.getURL() + ", "));
            System.out.println("");

            Application.election.clear();
            Application.election.add(participants);

            resp.status(200);
            return resp;
        });

        post(OurRoutes.JOIN.getPath(), (req, resp) -> {
            ElectionParticipant join = Application.election.join(converter.fromJson(req.body(), ElectionParticipant.class));
            LOG.info(">>> NEW PARTICIPANT: " + join);

            Set<ElectionParticipant> participants = Application.election.getParticipants();
            ElectionParticipant[] participantsArray = participants.toArray(new ElectionParticipant[participants.size()]);
            LOG.info("ARRAY: " + Arrays.toString(participantsArray));
            String participantsJSON = converter.toJson(participantsArray);

            for (ElectionParticipant next : participants) {
                Application.client.setTargetURL(next.getIp(), next.getPort());

                LOG.info("Sending all participants to " + next.getURL() + ":" + next.getPort());
                LOG.info("JSON:\n" + participantsJSON);

                Application.client.post(user,
                        OurRoutes.GROUP.getPath(),
                        participantsJSON);

                Application.client.setDefaultURL();
            }

            resp.status(200);
            return converter.toJson(join);
        });

        post(OurRoutes.COORDINATOR.getPath(), (req, resp) -> {
            String body = req.body();
            LOG.info(">>> COORDINATOR :  " + body);
            ElectionParticipant coordinator = converter.fromJson(body, ElectionParticipant.class);
            Application.election.setCoordinator(coordinator);
            resp.status(200);
            return resp;
        });
    }

    public ServiceEndpoint getEndpoint() {
        return endpoint;
    }

}