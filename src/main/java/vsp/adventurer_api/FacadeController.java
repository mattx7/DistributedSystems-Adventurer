package vsp.adventurer_api;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import org.apache.log4j.Logger;
import vsp.adventurer_api.entities.*;
import vsp.adventurer_api.http.api.OwnResourceHolder;

import static spark.Spark.get;
import static spark.Spark.post;

public enum FacadeController {
    Singleton;

    private static final Logger LOG = Logger.getLogger(FacadeController.class);

    private static final String ADVENTURER_URI = "/users/bastard";

    private final Gson converter = new Gson();

    WebResourceEntityCache<Group> groups = new WebResourceEntityCache<>(Group.class, OwnResourceHolder.GROUP);
    WebResourceEntityCache<Hiring> hirings = new WebResourceEntityCache<>(Hiring.class, OwnResourceHolder.HIRINGS);
    WebResourceEntityCache<Assignment> assignments = new WebResourceEntityCache<>(Assignment.class, OwnResourceHolder.ASSIGNMENTS);
    WebResourceEntityCache<Message> messages = new WebResourceEntityCache<>(Message.class, OwnResourceHolder.MESSAGES);

    public void run(User user) {

        // basic information
        get("/", (req, resp) -> converter.toJson(new ServiceEndpoint(ADVENTURER_URI, false)));

        // our adventurer
        get(ADVENTURER_URI, (req, resp) -> converter.toJson(user));

        Lists.<WebResourceEntityCache>newArrayList(groups, hirings, assignments, messages)
                .forEach(webResource -> get(
                        webResource.route(), (req, resp) -> converter.toJson(webResource.getObjects())));

        post(hirings.route(), (req, resp) -> converter.fromJson(resp.body(), Hiring.class));

    }
}