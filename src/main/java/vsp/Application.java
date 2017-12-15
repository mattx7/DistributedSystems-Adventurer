package vsp;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import vsp.adventurer_api.APIClient;
import vsp.adventurer_api.ServiceController;
import vsp.adventurer_api.custom_exceptions.HTTPConnectionException;
import vsp.adventurer_api.custom_exceptions.TokenNotFoundException;
import vsp.adventurer_api.election.BullyAlgorithm;
import vsp.adventurer_api.election.ElectionParticipant;
import vsp.adventurer_api.entities.Message;
import vsp.adventurer_api.entities.adventurer.Adventurer;
import vsp.adventurer_api.entities.adventurer.AdventurerWrapper;
import vsp.adventurer_api.entities.adventurer.CreateAdventurer;
import vsp.adventurer_api.entities.assignment.Assignment;
import vsp.adventurer_api.entities.assignment.TaskResult;
import vsp.adventurer_api.entities.basic.Token;
import vsp.adventurer_api.entities.basic.User;
import vsp.adventurer_api.entities.cache.Cache;
import vsp.adventurer_api.entities.group.CreatedGroup;
import vsp.adventurer_api.entities.group.Group;
import vsp.adventurer_api.entities.group.GroupWrapper;
import vsp.adventurer_api.entities.group.Hiring;
import vsp.adventurer_api.http.HTTPResponse;
import vsp.adventurer_api.http.api.BlackboardRoutes;
import vsp.adventurer_api.http.api.OurRoutes;
import vsp.adventurer_api.http.api.Route;
import vsp.adventurer_api.utility.BlackBoard;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Console;
import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

/**
 * Runs application and interactions.
 */
public class Application {
    private static Logger LOG = Logger.getLogger(Application.class);

    private static final int BLACKBOARD_PORT = 24000;
    public static final int OWN_PORT = 4567;

    /**
     * Will be set after start.
     */
    @Nullable
    public static String OWN_IP;

    @Nonnull
    private static final String AWAIT_COMMAND_MARKER = "#IN>";

    @Nonnull
    private static final Gson CONVERTER = new Gson();

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
    private static final String ASSIGN = "!assign";
    private static final String MEMBER = "!member";
    private static final String ASSIGNMENTS = "!assignments";
    private static final String RESULT = "!result";
    private static final String RESULTS = "!results";
    private static final String PARTICIPANTS = "!participants";
    private static final String ELECTION = "!election";

    /**
     * Holds the terminal for intern int- and output.
     */
    private static Console TERMINAL;

    /**
     * Holds client for everyone
     */
    public static APIClient CLIENT;

    /**
     * Our adventurer in the taverna.
     */
    public static CreateAdventurer adventurer;

    /**
     * Our user.
     */
    private static User user;

    /**
     * THe election algorithm.
     */
    public static BullyAlgorithm election = new BullyAlgorithm();

    /**
     * Holds only the main method an instance is not necessary.
     */
    private Application() {
    }

    public static void main(String[] args) {
        LOG.debug("Starting application...");

        try {
            BlackBoard blackBoard = new BlackBoard(BLACKBOARD_PORT);
            CLIENT = new APIClient(blackBoard.getHostAddress(), blackBoard.getPort());
            OWN_IP = InetAddress.getLocalHost().getHostAddress();

            // interactions
            TERMINAL = System.console();
            user = insertUser();
            LOG.debug("New user " + user.getName() + ":" + user.getPassword());

            // login or register
            handleRegisterIfNecessary(CLIENT, user);

            String heroClass = TERMINAL.readLine("Insert your heroclass: ");
            adventurer = new CreateAdventurer(heroClass, "", OWN_IP);

            // add link/json to taverna/adventurers
            postAdventurer(user);

            // Start rest-api
            ServiceController.SINGLETON.run(user);
            sleep();
            showHelpMessage();
            awaitAndHandleCommand(CLIENT, user);

        } catch (final IOException e) {
            LOG.error(e);
        }

    }

    private static void awaitAndHandleCommand(@Nonnull final APIClient client,
                                              @Nonnull final User user) {
        boolean holdAwaitCommandAlive = true;
        while (holdAwaitCommandAlive) {
            // input
            String[] parameter = TERMINAL.readLine(AWAIT_COMMAND_MARKER).split(" ");

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
                            break;
                        case HOST:
                            client.setDefaultURL();
                            print("Host changed to default");
                            break;
                        case VISITS:
                            print(client.get(user, BlackboardRoutes.VISITS).getJson());
                            break;
                        case TOKEN:
                            printToken(client);
                            break;
                        case GROUP:
                            final CreatedGroup createdGroup = client.post(user, BlackboardRoutes.GROUP, "").getAs(CreatedGroup.class);
                            Group newGroup = createdGroup.getGroup();

//                            print(client
//                                    .post(user, Route.concat(BlackboardRoutes.GROUP, String.valueOf(createdGroup.getId()), "members"), "")
//                                    .getJson());

                            Group group = client
                                    .get(user, Route.concat(BlackboardRoutes.GROUP, String.valueOf(newGroup.getId())))
                                    .getAs(GroupWrapper.class)
                                    .getObject();

                            Cache.GROUPS.add(group);

                            addCapabilities(user, "group", "election");

                            ServiceController.SINGLETON.getEndpoint().setGroup(Application.CLIENT.getDefaultURL().split("//")[1] + BlackboardRoutes.GROUP + "/" + group.getId());

                            // inti election as coordinator
                            ElectionParticipant yourself = new ElectionParticipant(OWN_IP, OWN_PORT);
                            election.join(yourself);
                            election.setYourself(yourself);


                            break;
                        case MEMBER:
                            updateGroupMembers(client, user);
                            try {
                                Group grp = Cache.GROUPS.getObjects().get(0);
                                StringBuilder strBuilder = new StringBuilder();
                                for (final String member : grp.getMembers()) {
                                    strBuilder.append(member).append(",");
                                }
                                print(String.valueOf(grp.getId()) + " -> " + strBuilder.toString());

                            } catch (Exception e) {
                                print("You are not in a group");
                            }

                            break;
                        case ASSIGNMENTS:
                            for (Assignment assignment : Cache.ASSIGNMENTS.getObjects()) {
                                print(assignment.toString());
                            }
                            break;
                        case RESULTS:
                            for (TaskResult result : Cache.RESULTS.getObjects()) {
                                print(result.toString());
                            }
                            break;
                        case PARTICIPANTS:
                            Set<ElectionParticipant> participants = election.getParticipants();
                            StringBuilder strBuilder = new StringBuilder();
                            print("PARTICIPANTS:\n");
                            for (final ElectionParticipant part : participants) {
                                strBuilder.append(part.getId()).append(") ").append(part.getURL()).append("\n");
                            }
                            print(strBuilder.toString());
                            break;
                        case ELECTION:
                            election.process();
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
                        case POST:
                            print(client.post(user, param2, "").getJson());
                            break;
                        case QUEST:
                            print(client.quest(user, param2).getJson());
                            break;
                        case SET_TOKEN:
                            user.setToken(client.getToken(param2));
                            print("Auth token is now set to " + param2);
                            break;
                        case VISITS:
                            print(client.post(user, BlackboardRoutes.VISITS, param2).getJson());
                            break;
                        case HOST:
                            if ("self".equalsIgnoreCase(param2)) {
                                client.setTargetURL(OWN_IP, OWN_PORT);
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
                        case DELIVER:
                            print(client.deliver(user, Integer.valueOf(param2), Integer.valueOf(param3), param4).getJson());
                            break;
                        case HIRING:
                            print(client.post(user, OurRoutes.HIRINGS,
                                    CONVERTER.toJson(new Hiring(BlackboardRoutes.GROUP + "/" + param2, param3, param4))).getJson());
                            break;
                        default:
                            showHelpMessage();
                            break;
                    }

                } else if (parameter.length == 5) {
                    switch (parameter[0]) {
                        case HIRING:
                            Adventurer adventurer = getAdventurer(client, parameter[1]);
                            String url = adventurer.getUrl();
                            LOG.info(">>> Sending hiring to " + url);
                            client.setTargetURL(url, OWN_PORT);

                            print(client.post(user, OurRoutes.HIRINGS,
                                    CONVERTER.toJson(new Hiring(BlackboardRoutes.GROUP + "/" + parameter[2], parameter[3], parameter[4]))).getJson());
                            client.setDefaultURL();
                            break;
                        case RESULT:
                            final String id = parameter[1];
                            Assignment assignment = null;
                            for (Assignment asnmt : Cache.ASSIGNMENTS.getObjects()) {
                                if (asnmt.getId().equalsIgnoreCase(id)) {
                                    assignment = asnmt;
                                }
                            }

                            if (assignment != null) {
                                final String json = CONVERTER.toJson(new TaskResult(assignment.getId(),
                                        assignment.getTask(),
                                        assignment.getResource(),
                                        parameter[2],
                                        parameter[3],
                                        BlackboardRoutes.USERS + "/" + user.getName(),
                                        parameter[4]), TaskResult.class);
                                LOG.debug("Result: " + json);
                                String[] split = assignment.getCallback().split(":");
                                client.setTargetURL(split[0], Integer.valueOf(split[1]));
                                client.post(user, OurRoutes.RESULTS, json);
                                client.setDefaultURL();
                            } else {
                                print(">>> No assignment found with the given ID <<<");
                            }
                            break;
                        default:
                            showHelpMessage();
                            break;
                    }
                } else if (parameter.length == 8) {
                    switch (parameter[0]) {
                        case ASSIGN:
                            final Adventurer adventurer = getAdventurer(client, parameter[1]);
                            client.setTargetURL(adventurer.getUrl(), OWN_PORT);
                            print(client.post(user, OurRoutes.ASSIGNMENTS,
                                    CONVERTER.toJson(new Assignment(parameter[2], parameter[3], parameter[4], parameter[5], parameter[6], OWN_IP + ":" + OWN_PORT, parameter[7]))).getJson());
                            client.setDefaultURL();
                            break;
                        default:
                            showHelpMessage();
                            break;
                    }
                }
            } catch (final IOException | NumberFormatException e) {
                LOG.error("", e);
            } catch (final TokenNotFoundException e) {
                LOG.error("Token not found!!!");
            } catch (final Exception e) {
                LOG.error("", e);
            }
        }
    }

    /**
     * Gets a adventurer from the taverna by name.
     */
    private static Adventurer getAdventurer(@Nonnull APIClient client, String name) throws IOException {
        final AdventurerWrapper wrapper = client.get(user, BlackboardRoutes.ADVENTURERS + "/" + name).getAs(AdventurerWrapper.class);
        return wrapper.getObject();
    }

    private static void sleep() {
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
        print(CLIENT.post(
                user,
                BlackboardRoutes.ADVENTURERS,
                CONVERTER.toJson(adventurer)).getJson());
    }

    @Nonnull
    private static User insertUser() {
        print("Please login or register with a username and password!");
        String username = TERMINAL.readLine("Username: ");
        String password = TERMINAL.readLine("password: ");
        return new User(username, password);
    }

    /**
     * Registers or logs a user into the blackboard.
     */
    private static void handleRegisterIfNecessary(@Nonnull final APIClient client,
                                                  @Nonnull final User user) throws IOException {
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

    /**
     * Adds new capabilities to the adventurer-service.
     *
     * @param user       Not null.
     * @param capability Not null
     * @throws IOException
     */
    public static void addCapabilities(@Nonnull User user, @Nonnull String... capability) throws IOException {
        Arrays.asList(capability).forEach(e -> adventurer.addCapabilities(e));
        postAdventurer(user);
    }

    private static void printToken(@Nonnull APIClient client) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : client.getTokenMap().entrySet()) {
            stringBuilder
                    .append(entry.getKey())
                    .append(" -> ")
                    .append(entry.getValue());
        }
        print(stringBuilder.toString());
    }

    private static void updateGroupMembers(@Nonnull APIClient client, @Nonnull User user) throws IOException {
        for (final Group group1 : Cache.GROUPS.getObjects()) {
            group1.setMembers(client.get(user, BlackboardRoutes.GROUP + "/" + group1.getId()).getAs(GroupWrapper.class).getObject().getMembers());
        }
    }

    private static void showHelpMessage() {
        print("# Basic commands: \n" +
                HELP + " - for this output \n" +
                QUIT + " - closes the terminal \n" +
                WHOAMI + " - information about me \n" +
                "# Questing: \n" +
                QUESTS + " - view open quests \n" +
                QUEST + " <id> - shows the quests \n" +
                MAP + " <location> - view the given location \n" +
                DELIVERIES + " <questId> - view delivery \n" +
                DELIVER + " <questID> <taskID> <tokenName> - delivers quest \n" +
                TASK + "\" <questId> - ??? \n" +
                HOST + " [<ip> <port>| self | <int> ] - change host if nothing set it will be changed to default \n" +
                NEW_TOKEN + " <key> <token> - saves a token under the key \n" +
                TOKEN + " - returns list of saved tokens \n" +
                SET_TOKEN + "  <key> - Sets a new token in the header \n" +
                VISITS + " [<body>] - Visits a location \n" +
                "# Grouping: \n" +
                HIRING + " [<adventurer>] <groupID> <quest> <message> \n" +
                GROUP + " - creates a new group and saves it \n" +
                MEMBER + " - lists members of the group \n" +
                PARTICIPANTS + " - lists all participants \n" +
                ELECTION + " - starts a election \n" +
                ASSIGN + " <username> <ID> <taskURI> <resourceURI> <method> <data> <message>\n" +
                ASSIGNMENTS + " - lists all assignments \n" +
                RESULT + " <ID> <method> <data> <message> \n" +
                RESULTS + " - lists all results \n" +
                "# Debug commands: \n" +
                GET + " <path> - GET on given path \n" +
                POST + " <path> <body> - POST with given path and body \n" +
                PUT + " <path> <body> - POST with given path and body \n"
        );
    }

    private static void print(@Nonnull String message) {
        System.out.println(message);
    }


    public static boolean acceptToNewHiring(String hiring) {
        print("Incoming Hiring:");
        print(hiring);
        return awaitHiringAnswer();
    }

    private static boolean awaitHiringAnswer() {
        while (true) {
            String parameter = TERMINAL.readLine("Write '!yes' or '!no' to accept the hiring or not: ");
            if ("!yes".equalsIgnoreCase(parameter)) {
                return true;
            } else if ("!no".equalsIgnoreCase(parameter)) {
                return false;
            } else {
                print("wrong answer! Only !yes or '!no' are 'allowed'");
            }
        }
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
            CLIENT.setTargetURL(participant.getIp(), participant.getPort());
            LOG.info("Sending election to " + participant);
            try {
                Message message = CLIENT.post(user, participant.getElectionRoute(), CONVERTER.toJson(election.getYourself())).getAs(Message.class);
                print(message != null ? participant.getIp() + ": " + message.getMessage() : "");
            } catch (IOException e) {
                print(participant.getIp() + " is offline!!!");
                offlineParticipants.add(participant);
            }
            CLIENT.backToOldTarget();
        }
        return offlineParticipants;
    }

    public static void sendCoordinator(ElectionParticipant yourself) {
        try {
            for (ElectionParticipant participant : election.getParticipants()) {
                CLIENT.setTargetURL(participant.getIp(), participant.getPort());
                print(CLIENT.post(user, OurRoutes.COORDINATOR, CONVERTER.toJson(yourself)).getJson());
                CLIENT.backToOldTarget();
            }
        } catch (IOException e) {
            LOG.error(e);
        }
    }
}