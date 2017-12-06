package vsp.adventurer_api;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import vsp.Application;
import vsp.adventurer_api.entities.Election;
import vsp.adventurer_api.entities.Message;
import vsp.adventurer_api.entities.assignment.Assignment;
import vsp.adventurer_api.entities.assignment.TaskResult;
import vsp.adventurer_api.entities.basic.ServiceEndpoint;
import vsp.adventurer_api.entities.basic.User;
import vsp.adventurer_api.entities.cache.Cache;
import vsp.adventurer_api.entities.cache.WebResourceEntityCache;
import vsp.adventurer_api.entities.group.Hiring;
import vsp.adventurer_api.http.HTTPResponse;
import vsp.adventurer_api.http.api.OurRoutes;

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
        Lists.<WebResourceEntityCache>newArrayList(Cache.GROUPS, Cache.HIRINGS, Cache.ASSIGNMENTS, Cache.MESSAGES)
                .forEach(webResource -> get(
                        webResource.route(), (req, resp) -> converter.toJson(webResource.getObjects())));

        // react to post on /hirings
        post(Cache.HIRINGS.route(), (req, resp) -> {
            LOG.debug("reqBody: " + req.body());
            final Hiring hiring;
            try {
                hiring = converter.fromJson(req.body(), Hiring.class);
                final boolean isAccepted = Application.acceptToNewHiring(req.body());
                if (isAccepted) {
//                    Cache.HIRINGS.add(hiring); TODO /hirings usefull?
                    Application.client.setDefaultURL(); // to blackboard in case of sending hiring to self
                    String groupRoute = hiring.getGroup();
                    getEndpoint().setGroup(Application.client.getDefaultURL().split("//")[1] + groupRoute);
                    final HTTPResponse response = Application.client.post(user, groupRoute + "/members", "");
                    Application.client.backToOldTarget();
                    LOG.debug("received json: \n" + response.getJson());
                    resp.status(200);
                    return converter.toJson(new Message("hiring accepted"));
                    // TODO some more HTTP codes
                } else {
                    return halt(406, converter.toJson(new Message("hiring rejected")));
                }
            } catch (JsonSyntaxException e) {
                return halt(401, "JsonSyntaxException");
            }
        });

        // react to post on /assignments
        post(Cache.ASSIGNMENTS.route(), (req, resp) -> {
            LOG.debug("reqBody: " + req.body());

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
            LOG.debug("received json: \n" + result);
            getEndpoint().setIdle(false);
            Cache.RESULTS.add(result);
            return resp;
        });

        post(Cache.ELECTIONS.route(), (req, resp) -> {
            final Election election = converter.fromJson(req.body(), Election.class);
            LOG.debug("received json: \n" + election);
            return resp;
        });
    }

    public ServiceEndpoint getEndpoint() {
        return endpoint;
    }

    public void updateAssignments() { // TODO does this work right? und what is with /assignments
        Cache.ASSIGNMENTS.getObjects().forEach(e -> get(Cache.ASSIGNMENTS.route() + "/" + e.getId(), (req, resp) -> converter.toJson(e)));
    }

}