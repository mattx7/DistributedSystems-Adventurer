package vsp;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import vsp.adventurer_api.APIClient;
import vsp.adventurer_api.FacadeController;
import vsp.adventurer_api.entities.Assignment;
import vsp.adventurer_api.entities.CreateAdventurer;
import vsp.adventurer_api.entities.Hiring;
import vsp.adventurer_api.entities.basic.Token;
import vsp.adventurer_api.entities.basic.User;
import vsp.adventurer_api.http.HTTPConnectionException;
import vsp.adventurer_api.http.HTTPResponse;
import vsp.adventurer_api.http.api.BlackboardRoutes;
import vsp.adventurer_api.http.api.OurRoutes;
import vsp.adventurer_api.utility.BlackBoard;

import java.io.Console;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

/**
 * Runs application and interactions.
 */
public class Application {
    private static Logger LOG = Logger.getLogger(Application.class);

    private static final int BLACKBOARD_PORT = 24000;
    private static final int OWN_PORT = 4567;
    private static final String AWAIT_COMMAND_MARKER = "#IN>";
    private static Gson jsonConverter = new Gson();

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
    private static final String HOST = "!host";
    private static final String QUEST = "!quest";
    private static final String NEW_TOKEN = "!newToken";
    private static final String TOKEN = "!token";
    private static final String SET_TOKEN = "!setToken";
    private static final String VISITS = "!visits";
    private static final String HIRING = "!hiring";
    private static final String GROUP = "!group";
    private static final String ASSIGNMENT = "!assignment";

    private static Console terminal;
    private static boolean holdAwaitCommandAlive;
    private static String ownIP;

    /**
     * Holds client for everyone
     */
    public static APIClient client;

    /**
     * Holds only the main method an instance is not necessary.
     */
    private Application() {
    }

    public static void main(String[] args) throws IOException {
        LOG.debug("Starting application...");

        try {
            BlackBoard blackBoard = new BlackBoard(BLACKBOARD_PORT);
            client = new APIClient(blackBoard.getHostAddress(), blackBoard.getPort());
            ownIP = InetAddress.getLocalHost().getHostAddress();

            // interactions
            terminal = System.console();
            User user = insertUser();
            LOG.debug("New user " + user.getName() + ":" + user.getPassword());

            // login or register
            handleRegisterIfNecessary(client, user);

            String heroclass = terminal.readLine("Insert your heroclass: ");

            // add link/json to taverna/adventurers
            joinTheTaverna(ownIP, user, heroclass);

            // Start rest-api
            FacadeController.Singleton.run(user, BlackboardRoutes.USERS.getPath() + "/" + user.getName());

            // At /taverna/groups one might post to create a new group. You have to join the group, even when you are the creator.
            showHelpMessage();
            awaitAndHandleCommand(client, user);

        } catch (final IOException e) {
            LOG.error(e);
        }


    }

    private static void joinTheTaverna(final String ownIP, final User user, final String heroclass) throws IOException {
        print(client.post(
                user,
                BlackboardRoutes.ADVENTURERS.getPath(),
                jsonConverter.toJson(new CreateAdventurer(heroclass, "", ownIP))).getJson());
    }

    @NotNull
    private static User insertUser() {
        print("Please login or register with a username and password!");
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

    private static void awaitAndHandleCommand(@NotNull final APIClient client,
                                              @NotNull final User user) throws IOException {
        holdAwaitCommandAlive = true;
        while (holdAwaitCommandAlive) {
            // input
            String[] parameter = terminal.readLine(AWAIT_COMMAND_MARKER).split(" ");

            try {
                if (parameter.length == 1) {
                    switch (parameter[0]) {
                        case QUIT:
                            print("BYE!");
                            holdAwaitCommandAlive = false;
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
                            print(client.get(user, BlackboardRoutes.VISITS.getPath()).getJson());
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
                        case GROUP:
                            print(client.post(user, BlackboardRoutes.GROUP.getPath(), "").getJson()); // TODO save group id
                            break;
                        default:
                            showHelpMessage();
                            break;
                    }
                } else if (parameter.length == 2) {
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
                            user.setToken(client.getToken(param2));
                            print("Auth token is now set to " + param2);
                            break;
                        case VISITS:
                            print(client.post(user, BlackboardRoutes.VISITS.getPath(), param2).getJson());
                            break;
                        case HOST:
                            if ("self".equalsIgnoreCase(param2)) {
                                client.setTargetURL(ownIP, OWN_PORT);
                                print("Host changed to own ip");
                            } else {
                                try {
                                    final Integer ipEnding = Integer.valueOf(param2);
                                    client.setTargetURL("172.19.0." + ipEnding, OWN_PORT);
                                    print("Host changed to 172.19.0." + ipEnding);
                                } catch (NumberFormatException e) {
                                    showHelpMessage();
                                }
                            }
                            break;
                        default:
                            showHelpMessage();
                            break;
                    }
                } else if (parameter.length == 3) {
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
                        case HIRING:
                            print(client.post(user, OurRoutes.HIRINGS.getPath(),
                                    jsonConverter.toJson(new Hiring(BlackboardRoutes.GROUP.getPath() + "/" + param2, param3, param4))).getJson());
                            break;
                        default:
                            showHelpMessage();
                            break;
                    }

                } else if (parameter.length == 8) {
                    switch (parameter[0]) {
                        case ASSIGNMENT:
                            print(client.post(user, OurRoutes.ASSIGNMENTS.getPath(),
                                    jsonConverter.toJson(new Assignment(parameter[1], parameter[2], parameter[3], parameter[4], parameter[5], parameter[6], parameter[7]))).getJson());
                            break;
                        default:
                            showHelpMessage();
                            break;
                    }

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

    private static void showHelpMessage() {
        print("Basic commands: \n" +
                HELP + " - for this output \n" +
                QUIT + " - closes the terminal \n" +
                WHOAMI + " - information about me \n" +
                "Questing: \n" +
                QUESTS + " - view open quests \n" +
                QUEST + " <id> - shows the quets \n" +
                MAP + " <location> - view the given location \n" +
                DELIVERIES + " <questId> - view delivery \n" +
                DELIVER + " <questID> <taskID> <tokenName> - delivers quest" +
                TASK + "\" <questId> - ??? \n" +
                HOST + " [<ip> <port>| self | <int> ] - change host if nothing set it will be changed to default \n" +
                NEW_TOKEN + " <key> <token> - saves a token under the key \n" +
                TOKEN + " - returns list of saved tokens \n" +
                SET_TOKEN + "  <key> - Sets a new token in the header \n" +
                VISITS + " [<body>] - Visits a location \n" +
                "Grouping: \n" +
                HIRING + " <groupID> <quest> <message> \n" +
                GROUP + " - create a new group \n" +
                ASSIGNMENT + " <ID> <taskURI> <resourceURI> <method> <data> <callbackURI> <message>\n" +
                "Debug commands: \n" +
                GET + " <path> - GET on given path \n" +
                POST + " <path> <body> - POST with given path and body \n" +
                PUT + " <path> <body> - POST with given path and body \n"
        );
    }

    private static void print(@NotNull String message) {
        System.out.println(message);
    }


    public static boolean acceptToNewHiring(String hiring) {
        holdAwaitCommandAlive = false;
        print("Incoming Hiring:");
        print(hiring);
        return awaitHiringResponse();
    }

    private static boolean awaitHiringResponse() {
        while (true) {
            String parameter = terminal.readLine("Write '!yes' or '!no' to accept the hiring or not: ");
            if ("!yes".equalsIgnoreCase(parameter)) {
                return true;
            } else if ("!no".equalsIgnoreCase(parameter)) {
                return false;
            } else {
                print("wrong answer! Only !yes or '!no' are 'allowed'");
            }
        }
    }
}