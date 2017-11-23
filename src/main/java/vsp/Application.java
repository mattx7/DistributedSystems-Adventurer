package vsp;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import vsp.adventurer_api.APIClient;
import vsp.adventurer_api.FacadeController;
import vsp.adventurer_api.entities.Token;
import vsp.adventurer_api.entities.User;
import vsp.adventurer_api.http.HTTPConnectionException;
import vsp.adventurer_api.http.HTTPResponse;
import vsp.adventurer_api.utility.BlackBoard;

import java.io.Console;
import java.io.IOException;

/**
 * Runs application and interactions.
 */
public class Application {
    private static final int BLACKBOARD_PORT = 24000;
    private static Logger LOG = Logger.getLogger(Application.class);
    private static Console terminal;

    /**
     * Holds only the main method an instance is not necessary.
     */
    private Application() {
    }

    public static void main(String[] args) throws IOException {
        LOG.debug("Starting application...");

        BlackBoard blackBoard = new BlackBoard(BLACKBOARD_PORT);

        try {
            APIClient client = new APIClient(blackBoard.getHostAddress(), blackBoard.getPort());
            // interactions
            User user = insertUser();
            LOG.debug("New user " + user.getName() + ":" + user.getPassword());
            handleRegisterIfNecessary(client, user);
        } catch (final IOException e) {
            LOG.error(e);
        }

        // TODO create adventurer in the taverna

        // Start rest-api
        FacadeController.Singleton.run();
    }

    @NotNull
    private static User insertUser() {
        print("Please login or register with a username and password!");
        terminal = System.console();
        String username = terminal.readLine("Username: ");
        String password = terminal.readLine("password: ");
        return new User(username, password);
    }

    private static void handleRegisterIfNecessary(@NotNull final APIClient client,
                                                  @NotNull final User user) throws IOException {
        try {
            final HTTPResponse response = client.register(user);
            LOG.debug(response);
            print("User is now registered!");
        } catch (final IOException e) {
            if (e instanceof HTTPConnectionException && ((HTTPConnectionException) e).getErrorCode() == 409)
                print("User already registered!");
            else
                LOG.error(e.getMessage());
        }

        // login
        final Token token = client.login(user).getAs(Token.class);
        user.setToken(token);
        print((token == null) ? "Login failed!" : "User logged in");
        client.saveToken("default", token.getToken()); // TODO NPE
    }

    private static void print(@NotNull String message) {
        System.out.println(message);
    }


}