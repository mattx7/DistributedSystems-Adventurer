package vsp.adventurer_api;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.log4j.Logger;
import vsp.Application;
import vsp.adventurer_api.entities.Assignment;
import vsp.adventurer_api.entities.Hiring;
import vsp.adventurer_api.entities.Message;
import vsp.adventurer_api.entities.basic.ServiceEndpoint;
import vsp.adventurer_api.entities.basic.User;
import vsp.adventurer_api.entities.cache.Cache;
import vsp.adventurer_api.entities.cache.WebResourceEntityCache;
import vsp.adventurer_api.entities.group.GroupWrapper;
import vsp.adventurer_api.http.HTTPResponse;

import static spark.Spark.*;

public enum FacadeController {
    Singleton;

    private static final Logger LOG = Logger.getLogger(FacadeController.class);

    private final Gson converter = new Gson();

    public void run(User user, String userURI) {

        // basic information
        get("/", (req, resp) -> converter.toJson(new ServiceEndpoint(userURI, false)));

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
//                    Cache.HIRINGS.add(hiring);

                    Application.client.setDefaultURL(); // to blackboard in case of sending hiring to self
                    final HTTPResponse response = Application.client.post(user, hiring.getGroup() + "/members", ""); // TODO path um iwie group z holen oder so
                    Application.client.backToOldTarget();

                    LOG.debug("###GROUP###: \n" + response.getJson());
                    final GroupWrapper wrapper = converter.fromJson(response.getJson(), GroupWrapper.class); // TODO dies macht auch 0 Sinn
                    LOG.debug("object: " + wrapper.getObject());
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
            LOG.debug("reqBody: " + req.body());
            final Assignment assignment;
            try {
                assignment = converter.fromJson(req.body(), Assignment.class);
                Cache.ASSIGNMENTS.add(assignment);
                resp.status(200);
                return resp;
            } catch (JsonSyntaxException e) {
                return halt(401, "JsonSyntaxException");
            }

        });

    }
}