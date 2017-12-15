package vsp.adventurer_api;


import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import vsp.Application;
import vsp.adventurer_api.custom_exceptions.TokenNotFoundException;
import vsp.adventurer_api.entities.basic.Token;
import vsp.adventurer_api.entities.basic.User;
import vsp.adventurer_api.http.HTTPRequest;
import vsp.adventurer_api.http.HTTPResponse;
import vsp.adventurer_api.http.HTTPVerb;
import vsp.adventurer_api.http.api.BlackboardRoutes;
import vsp.adventurer_api.http.api.Route;
import vsp.adventurer_api.http.auth.HTTPBasicAuth;
import vsp.adventurer_api.http.auth.HTTPTokenAuth;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Offers operations from the given API.
 */
public class APIClient {

    private static Logger LOG = Logger.getLogger(Application.class);

    @Nonnull
    private static final String PROTOCOL = "http";

    private String temp;

    @Nonnull
    private String targetURL;

    @Nonnull
    private String defaultURL;

    @Nonnull
    private Map<String, String> tokenMap = new HashMap<>();

    public APIClient(@Nonnull final String restApiAddress,
                     @Nonnull final Integer restApiPort) {
        Preconditions.checkNotNull(restApiAddress, "restApiAddress should not be null.");
        Preconditions.checkNotNull(restApiPort, "restApiPort should not be null.");

        this.targetURL = String.format("%s://%s:%d", PROTOCOL, restApiAddress, restApiPort);
        this.defaultURL = targetURL;
        LOG.debug("URL: " + targetURL);
    }

    public void setTargetURL(@Nonnull String restApiAddress, @Nonnull final Integer restApiPort) {
        this.targetURL = String.format("%s://%s:%d", PROTOCOL, restApiAddress, restApiPort);
    }

    public void setDefaultURL() {
        this.temp = this.targetURL;
        this.targetURL = defaultURL;
    }

    @Nonnull
    public String getDefaultURL() {
        return defaultURL;
    }

    public void backToOldTarget() {
        this.targetURL = this.temp;
    }

    // ======= for debug/testing ======

    public HTTPResponse get(@Nonnull final User user,
                            @Nonnull final String path) throws IOException {
        LOG.debug("Registration with user " + user.getName());
        return HTTPRequest
                .to(targetURL)
                .resource(path)
                .type(HTTPVerb.GET)
                .auth(HTTPTokenAuth.forUser(user))
                .send();
    }

    public HTTPResponse post(@Nonnull final User user,
                             @Nonnull final String path,
                             @Nonnull final String body) throws IOException {
        LOG.debug("Registration with user " + user.getName());
        return HTTPRequest
                .to(targetURL)
                .resource(path)
                .type(HTTPVerb.POST)
                .auth(HTTPTokenAuth.forUser(user))
                .body(body)
                .send();
    }

    public HTTPResponse put(@Nonnull final User user,
                            @Nonnull final String path,
                            @Nonnull final String body) throws IOException {
        LOG.debug("Registration with user " + user.getName());
        return HTTPRequest
                .to(targetURL)
                .resource(path)
                .type(HTTPVerb.PUT)
                .auth(HTTPTokenAuth.forUser(user))
                .body(body)
                .send();
    }

    //  =====================================

    /**
     * Registers a user to the blackboard.
     *
     * @param user Not null.
     * @return TODO
     * @throws IOException If connection fails.
     */
    public HTTPResponse register(@Nonnull final User user) throws IOException {
        LOG.debug("Registration with user " + user.getName());
        return HTTPRequest
                .to(targetURL)
                .resource(BlackboardRoutes.USERS)
                .type(HTTPVerb.POST)
                .body(user)
                .send();
    }

    /**
     * Login to receive a authentication token.
     *
     * @param user Not null.
     * @return TODO
     * @throws IOException If connection fails.
     */
    public HTTPResponse login(@Nonnull final User user) throws IOException {
        LOG.debug("Login with user '" + user.getName() + "'...");
        return HTTPRequest
                .to(targetURL)
                .resource(BlackboardRoutes.LOGIN)
                .type(HTTPVerb.GET)
                .auth(HTTPBasicAuth.forUser(user))
                .send();
    }


    /**
     * Checks given user.
     *
     * @param user Not null.
     * @return TODO
     * @throws IOException If connection fails.
     */
    public HTTPResponse whoAmI(@Nonnull final User user) throws IOException {
        LOG.debug("WhoAmI with user " + user.getName());
        return HTTPRequest
                .to(targetURL)
                .resource(BlackboardRoutes.WHOAMI)
                .type(HTTPVerb.GET)
                .auth(HTTPTokenAuth.forUser(user))
                .send();
    }

    public HTTPResponse quests(@Nonnull User user) throws IOException {
        LOG.debug("View quests");
        return HTTPRequest
                .to(targetURL)
                .resource(BlackboardRoutes.QUESTS)
                .type(HTTPVerb.GET)
                .auth(HTTPTokenAuth.forUser(user))
                .send();
    }

    public HTTPResponse quest(User user, String questId) throws IOException {
        LOG.debug("View quest with id: " + questId);
        return HTTPRequest
                .to(targetURL)
                .resource(Route.concat(BlackboardRoutes.QUESTS,
                        String.valueOf(questId)))
                .type(HTTPVerb.GET)
                .auth(HTTPTokenAuth.forUser(user))
                .send();
    }

    public HTTPResponse questDeliveries(@Nonnull final User user,
                                        @Nonnull final Integer questId) throws IOException {
        LOG.debug("View deliveries");
        return HTTPRequest
                .to(targetURL)
                .resource(Route.concat(
                        BlackboardRoutes.QUESTS,
                        String.valueOf(questId),
                        "deliveries"))
                .type(HTTPVerb.GET)
                .auth(HTTPTokenAuth.forUser(user))
                .send();
    }

    public HTTPResponse deliver(@Nonnull final User user,
                                @Nonnull final Integer questId,
                                @Nonnull final Integer taskId,
                                @Nonnull final String tokenKey) throws IOException, TokenNotFoundException {
        LOG.debug("View deliveries");
        return HTTPRequest
                .to(targetURL)
                .resource(Route.concat(
                        BlackboardRoutes.QUESTS,
                        String.valueOf(questId),
                        "deliveries"))
                .type(HTTPVerb.POST)
                .auth(HTTPTokenAuth.forUser(user))
                .body("{\"tokens\":{\"/blackboard/tasks/" + taskId + "\":\"" + getToken(tokenKey).getToken() + "\"}}")
                .send();
    }

    public HTTPResponse questTasks(@Nonnull final User user,
                                   @Nonnull final Integer questId) throws IOException {
        LOG.debug("View task");
        return HTTPRequest
                .to(targetURL)
                .resource(Route.concat(
                        BlackboardRoutes.QUESTS,
                        String.valueOf(questId),
                        "tasks"))
                .type(HTTPVerb.GET)
                .auth(HTTPTokenAuth.forUser(user))
                .send();
    }
    // TODO map

    public HTTPResponse map(@Nonnull final User user,
                            @Nonnull final String location) throws IOException {
        LOG.debug("View quests");
        return HTTPRequest
                .to(targetURL)
                .resource(Route.concat(BlackboardRoutes.MAP, location))
                .type(HTTPVerb.GET)
                .auth(HTTPTokenAuth.forUser(user))
                .send();
    }

    public void saveToken(String name, String token) {
        tokenMap.put(name, token);
        LOG.debug("Saved token " + name + "!");
    }

    @Nonnull
    public Map<String, String> getTokenMap() {
        return tokenMap;
    }

    public Token getToken(String key) throws TokenNotFoundException {
        String token = tokenMap.get(key);
        if (token == null)
            throw new TokenNotFoundException();
        return new Token(token, null);
    }
}
