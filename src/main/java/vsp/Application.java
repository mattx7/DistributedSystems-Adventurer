package vsp;

import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import vsp.api_client.APIClient;
import vsp.api_client.entities.Token;
import vsp.api_client.entities.User;
import vsp.api_client.http.HTTPConnectionException;
import vsp.api_client.http.HTTPResponse;
import vsp.api_client.utility.BlackBoard;

import java.io.Console;
import java.io.IOException;
import java.util.Map;

/**
 * Runs application and interactions.
 */
public class Application {
    private static final String HELP = "!help";
    private static final String WHOAMI = "!whoami";
    private static final String QUESTS = "!quests";
    private static final String MAP = "!map";
    private static final String DELIVER = "!deliver";
    private static final String DELIVERIES = DELIVER + "ies";
    private static final String TASK = "!task";
    private static final String QUIT = "!quit";
    private static final String GET = "!get";
    private static final String POST = "!post";
    private static final String PUT = "!put";

    private static final String AWAIT_COMMAND_MARKER = "#IN>";
    private static final String HOST = "!host";
    private static final String QUEST = "!quest";
    private static final String NEW_TOKEN = "!newToken";
    private static final String TOKEN = "!token";
    private static final String SET_TOKEN = "!setToken";
    private static final String VISITS = "!visits";
    private static Logger LOG = Logger.getLogger(Application.class);

    /**
     * Given port for the blackboard.
     */
    private static final int BLACKBOARD_PORT = 24000;
    private static Console terminal;

    /**
     * Holds only the main method an instance is not necessary.
     */
    private Application() {
    }

    public static void main(String[] args) throws IOException {
        print("Starting application...");
        BlackBoard blackBoard = new BlackBoard(BLACKBOARD_PORT);

        try {
            APIClient client = new APIClient(blackBoard.getHostAddress(), blackBoard.getPort());

            // interactions
            User user = insertUser();
            LOG.debug("New user " + user.getName() + ":" + user.getPassword());
            handleRegisterIfNecessary(client, user);
            showHelpMessage();
            awaitAndHandleCommand(client, user);
        } catch (final IOException e) {
            LOG.error(e);
        }
    }

    private static void awaitAndHandleCommand(@NotNull final APIClient client,
                                              @NotNull final User user) throws IOException {
        boolean holdAlive = true;
        while (holdAlive) {
            // input
            String[] parameter = terminal.readLine(AWAIT_COMMAND_MARKER).split(" ");

            try {
                if (parameter.length == 1) {
                    // commands with one param
                    switch (parameter[0]) {
                        case QUIT:
                            print("BYE!");
                            holdAlive = false;
                            break;
                        case WHOAMI:
                            print("whoAmI...");
                            print(client.whoAmI(user).getJson());
                            break;
                        case QUESTS:
                            print("Quests...");
                            print(client.quests(user).getJson());
                            /*  client
                                    .quests()
                                    .getAs(QuestWrapper.class) // TODO make null safe
                                    .getObjects()
                                    .stream()
                                    .map(e -> e.getId() + ": " + e.getName())
                                    .forEach(Application::print);*/
                            break;
                        case HOST:
                            client.setDefaultURL();
                            print("Host changed to default");
                            break;
                        case VISITS:
                            print(client.get(user, "/visits").getJson());
                            break;
                        case TOKEN:
                            final StringBuilder stringBuilder = new StringBuilder();
                            for (Map.Entry<String, String> entry : client.getTokenMap().entrySet()) {
                                stringBuilder
                                        .append(entry.getKey())
                                        .append(" -> ")
                                        .append(entry.getValue());
                            }
                            print(stringBuilder.toString());
                            break;
                        default:
                            showHelpMessage();
                            break;
                    }
                } else if (parameter.length == 2) {
                    // commands with two params
                    String param1 = parameter[0];
                    String param2 = parameter[1];

                    switch (param1) {
                        case MAP:
                            print("Map...");
                            print(client.map(user, param2).getJson());
                            break;
                        case DELIVERIES:
                            print("Deliveries...");
                            print(client
                                    .questDeliveries(user, Integer.valueOf(param2))
                                    .getJson());
                            break;
                        case TASK:
                            print("Task...");
                            print(client
                                    .questTasks(user, Integer.valueOf(param2))
                                    .getJson());
                            break;
                        case GET:
                            print(client.get(user, param2).getJson());
                            break;
                        case QUEST:
                            print(client.quest(user, param2).getJson());
                            break;
                        case SET_TOKEN:
                            user.setToken(client.getToken(param2)); // TODO BAD design token nicht im user ändern
                            print("Auth token is now set to " + param2);
                            break;
                        case VISITS:
                            print(client.post(user, "/visits", param2).getJson());
                            break;
                        default:
                            showHelpMessage();
                            break;
                    }
                } else if (parameter.length == 3) {
                    // commands with three params
                    String param1 = parameter[0];
                    String param2 = parameter[1];
                    String param3 = parameter[2];

                    switch (param1) {
                        case POST:
                            print(client.post(user, param2, param3).getJson());
                            break;
                        case PUT:
                            print(client.put(user, param2, param3).getJson());
                            break;
                        case HOST:
                            client.setTargetURL(param2, Integer.valueOf(param3));
                            print("Host changed to " + param2 + ":" + param3);
                            break;
                        case NEW_TOKEN:
                            client.saveToken(param2, param3);
                            break;
                        default:
                            showHelpMessage();
                            break;
                    }

                } else if (parameter.length == 4) {
                    // commands with three params
                    String param1 = parameter[0];
                    String param2 = parameter[1];
                    String param3 = parameter[2];
                    String param4 = parameter[3];

                    switch (param1) {
                        case DELIVER:
                            print(client.deliver(user, Integer.valueOf(param2), Integer.valueOf(param3), param4).getJson());
                            break;
                        default:
                            showHelpMessage();
                            break;
                    }
                } else {
                    showHelpMessage();
                }
            } catch (final IOException | NumberFormatException e) {
                LOG.error(e);
            } catch (final TokenNotFoundException e) {
                LOG.error("Token not found!!!");
            } catch (final Exception e) {
                LOG.error(e);
            }
        }
    }

    @NotNull
    private static User insertUser() {
        print("Please login or register with a username and password!");
        terminal = System.console();
        String username = terminal.readLine("Username: ");
        String password = terminal.readLine("password: ");
        return new User(username, password);
    }

    private static void showHelpMessage() {
        print("Possible commands: \n" +
                HELP + " - for this output \n" +
                QUIT + " - closes the terminal \n" +
                WHOAMI + " - information about me \n" +
                QUESTS + " - view open quests \n" +
                QUEST + " <id> - shows the quets \n" +
                MAP + " <location> - view the given location \n" +
                DELIVERIES + " <questId> - view delivery \n" +
                DELIVER + " <questID> <taskID> <tokenName> - delivers quest" +
                TASK + "\" <questId> - ??? \n" +
                HOST + " [<ip> <port>] - change host if nothing set it will be changed to default \n" +
                NEW_TOKEN + " <key> <token> - saves a token under the key \n" +
                TOKEN + " - returns list of saved tokens \n" +
                SET_TOKEN + "  <key> - Sets a new token in the header \n" +
                VISITS + " [<body>] - Visits a location \n" +
                "Debug commands: \n" +
                GET + " <path> - GET on given path \n" +
                POST + " <path> <body> - POST with given path and body \n" +
                PUT + " <path> <body> - POST with given path and body \n"
        );
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
