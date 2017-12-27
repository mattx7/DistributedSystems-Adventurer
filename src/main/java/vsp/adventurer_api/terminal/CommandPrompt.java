package vsp.adventurer_api.terminal;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import vsp.Application;
import vsp.adventurer_api.APIClient;
import vsp.adventurer_api.ServiceController;
import vsp.adventurer_api.cache.Cache;
import vsp.adventurer_api.custom_exceptions.HTTPConnectionException;
import vsp.adventurer_api.custom_exceptions.TokenNotFoundException;
import vsp.adventurer_api.election.ElectionParticipant;
import vsp.adventurer_api.entities.adventurer.Adventurer;
import vsp.adventurer_api.entities.adventurer.CreateAdventurer;
import vsp.adventurer_api.entities.assignment.Assignment;
import vsp.adventurer_api.entities.assignment.TaskResult;
import vsp.adventurer_api.entities.basic.Token;
import vsp.adventurer_api.entities.basic.User;
import vsp.adventurer_api.entities.group.CreatedGroup;
import vsp.adventurer_api.entities.group.Group;
import vsp.adventurer_api.entities.group.GroupWrapper;
import vsp.adventurer_api.entities.group.Hiring;
import vsp.adventurer_api.http.HTTPResponse;
import vsp.adventurer_api.http.api.BlackboardRoutes;
import vsp.adventurer_api.http.api.OurRoutes;
import vsp.adventurer_api.http.api.Route;
import vsp.adventurer_api.utility.BlackBoard;
import vsp.adventurer_api.utility.Capabilities;

import javax.annotation.Nonnull;
import java.io.Console;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;
import java.util.Set;

import static vsp.Application.*;

public class CommandPrompt {
    private static final Logger LOG = Logger.getLogger(CommandPrompt.class);

    @Nonnull
    private static final String AWAIT_COMMAND_MARKER = "#IN>";

    @Nonnull
    private static final Gson CONVERTER = new Gson();

    /**
     * Holds the terminal for intern int- and output.
     */
    private static Console TERMINAL;

    public void run() {
        try {
            BlackBoard blackBoard = new BlackBoard(Application.BLACKBOARD_PORT);
            Application.client = new APIClient(blackBoard.getHostAddress(), blackBoard.getPort());
            Application.ownIp = InetAddress.getLocalHost().getHostAddress();

            // interactions
            TERMINAL = System.console();
            Application.user = insertUser();
            LOG.debug("New user " + Application.user.getName() + ":" + Application.user.getPassword());

            // login or register
            handleRegisterIfNecessary(Application.client, Application.user);

            String heroClass = TERMINAL.readLine("Insert your heroclass: ");
            Application.adventurer = new CreateAdventurer(heroClass, Capabilities.MUTEX, APIClient.PROTOCOL + "://" + Application.ownIp + ":" + Application.OWN_PORT);

            // add link/json to taverna/adventurers
            Application.postAdventurer(Application.user);

            // Start rest-api
            ServiceController.SINGLETON.run(Application.user);
            Application.mutexAlgorithm.prepare();

            Application.sleep();
            Commands.showHelpMessage();
            awaitAndHandleCommand(Application.client, Application.user);

        } catch (final IOException e) {
            LOG.error(e);
        }
    }


    // ======== private ===========

    private void awaitAndHandleCommand(@Nonnull final APIClient client,
                                       @Nonnull final User user) {
        boolean holdAwaitCommandAlive = true;
        while (holdAwaitCommandAlive) {
            // input
            String[] parameter = TERMINAL.readLine(AWAIT_COMMAND_MARKER).split(" ");

            try {
                if (parameter.length == 1) {
                    switch (parameter[0]) {
                        case Commands.QUIT:
                            print("BYE!");
                            holdAwaitCommandAlive = false;
                            break;
                        case Commands.WHOAMI:
                            print("whoAmI...");
                            print(client.whoAmI(user).getJson());
                            break;
                        case Commands.QUESTS:
                            print("Quests...");
                            print(client.quests(user).getJson());
                            break;
                        case Commands.HOST:
                            client.setDefaultURL();
                            print("Host changed to default");
                            break;
                        case Commands.VISITS:
                            print(client.get(user, BlackboardRoutes.VISITS).getJson());
                            break;
                        case Commands.TOKEN:
                            printToken(client);
                            break;
                        case Commands.GROUP:
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

                            Application.addCapabilities(Capabilities.GROUP, Capabilities.ELECTION);

                            ServiceController.SINGLETON.getEndpoint().setGroup(Application.client.getDefaultURL().split("//")[1] + BlackboardRoutes.GROUP + "/" + group.getId());

                            // inti election as coordinator
                            ElectionParticipant yourself = new ElectionParticipant(ownIp, Application.OWN_PORT);
                            election.join(yourself);
                            election.setYourself(yourself);


                            break;
                        case Commands.MEMBER:
                            Application.updateGroupMembers(client, user);
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
                        case Commands.ASSIGNMENTS:
                            for (Assignment assignment : Cache.ASSIGNMENTS.getObjects()) {
                                print(assignment.toString());
                            }
                            break;
                        case Commands.RESULTS:
                            for (TaskResult result : Cache.RESULTS.getObjects()) {
                                print(result.toString());
                            }
                            break;
                        case Commands.PARTICIPANTS:
                            Set<ElectionParticipant> participants = election.getParticipants();
                            StringBuilder strBuilder = new StringBuilder();
                            print("PARTICIPANTS:\n");
                            for (final ElectionParticipant part : participants) {
                                strBuilder.append(part.getId()).append(") ").append(part.getURL()).append("\n");
                            }
                            print(strBuilder.toString());
                            break;
                        case Commands.ELECTION:
                            election.process();
                            break;
                        case Commands.PREPARE_MUTEX:
                            try {
                                Application.mutexAlgorithm.prepare();
                            } catch (IOException e) {
                                LOG.error("Err:", e);
                            }
                            break;
                        case Commands.MUTEX:
                            Application.mutexAlgorithm.requestAndAccess();
                            break;
                        case Commands.LEAVE_MUTEX:
                            Application.mutexAlgorithm.leaveMutex();
                            break;
                        default:
                            Commands.showHelpMessage();
                            break;
                    }
                } else if (parameter.length == 2) {
                    String param1 = parameter[0];
                    String param2 = parameter[1];

                    switch (param1) {
                        case Commands.MAP:
                            print("Map...");
                            print(client.map(user, param2).getJson());
                            break;
                        case Commands.DELIVERIES:
                            print("Deliveries...");
                            print(client
                                    .questDeliveries(user, Integer.valueOf(param2))
                                    .getJson());
                            break;
                        case Commands.TASK:
                            print("Task...");
                            print(client
                                    .questTasks(user, Integer.valueOf(param2))
                                    .getJson());
                            break;
                        case Commands.GET:
                            print(client.get(user, param2).getJson());
                            break;
                        case Commands.POST:
                            print(client.post(user, param2, "").getJson());
                            break;
                        case Commands.QUEST:
                            print(client.quest(user, param2).getJson());
                            break;
                        case Commands.SET_TOKEN:
                            user.setToken(client.getToken(param2));
                            print("Auth token is now set to " + param2);
                            break;
                        case Commands.VISITS:
                            print(client.post(user, BlackboardRoutes.VISITS, param2).getJson());
                            break;
                        case Commands.HOST:
                            if ("self".equalsIgnoreCase(param2)) {
                                client.setTargetURL(ownIp, OWN_PORT);
                                print("Host changed to own ip");
                            } else {
                                try {
                                    final Integer ipEnding = Integer.valueOf(param2);
                                    client.setTargetURL("172.19.0." + ipEnding, OWN_PORT);
                                    print("Host changed to 172.19.0." + ipEnding);
                                } catch (NumberFormatException e) {
                                    Commands.showHelpMessage();
                                }
                            }
                            break;
                        default:
                            Commands.showHelpMessage();
                            break;
                    }
                } else if (parameter.length == 3) {
                    String param1 = parameter[0];
                    String param2 = parameter[1];
                    String param3 = parameter[2];

                    switch (param1) {
                        case Commands.POST:
                            print(client.post(user, param2, param3).getJson());
                            break;
                        case Commands.PUT:
                            print(client.put(user, param2, param3).getJson());
                            break;
                        case Commands.HOST:
                            client.setTargetURL(param2, Integer.valueOf(param3));
                            print("Host changed to " + param2 + ":" + param3);
                            break;
                        case Commands.NEW_TOKEN:
                            client.saveToken(param2, param3);
                            break;
                        default:
                            Commands.showHelpMessage();
                            break;
                    }

                } else if (parameter.length == 4) {
                    // commands with three params
                    String param1 = parameter[0];
                    String param2 = parameter[1];
                    String param3 = parameter[2];
                    String param4 = parameter[3];

                    switch (param1) {
                        case Commands.DELIVER:
                            print(client.deliver(user, Integer.valueOf(param2), Integer.valueOf(param3), param4).getJson());
                            break;
                        case Commands.HIRING:
                            print(client.post(user, OurRoutes.HIRINGS,
                                    CONVERTER.toJson(new Hiring(BlackboardRoutes.GROUP + "/" + param2, param3, param4))).getJson());
                            break;
                        default:
                            Commands.showHelpMessage();
                            break;
                    }

                } else if (parameter.length == 5) {
                    switch (parameter[0]) {
                        case Commands.HIRING:
                            Adventurer adventurer = Application.getAdventurer(client, parameter[1]);
                            String url = adventurer.getUrl();
                            LOG.info(">>> Sending hiring to " + url);
                            client.setTargetURL(url, OWN_PORT);

                            print(client.post(user, OurRoutes.HIRINGS,
                                    CONVERTER.toJson(new Hiring(BlackboardRoutes.GROUP + "/" + parameter[2], parameter[3], parameter[4]))).getJson());
                            client.setDefaultURL();
                            break;
                        case Commands.RESULT:
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
                            Commands.showHelpMessage();
                            break;
                    }
                } else if (parameter.length == 8) {
                    switch (parameter[0]) {
                        case Commands.ASSIGN:
                            final Adventurer adventurer = getAdventurer(client, parameter[1]);
                            client.setTargetURL(adventurer.getUrl(), OWN_PORT);
                            print(client.post(user, OurRoutes.ASSIGNMENTS,
                                    CONVERTER.toJson(new Assignment(parameter[2], parameter[3], parameter[4], parameter[5], parameter[6], ownIp + ":" + OWN_PORT, parameter[7]))).getJson());
                            client.setDefaultURL();
                            break;
                        default:
                            Commands.showHelpMessage();
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

    @Nonnull
    private User insertUser() {
        print("Please login or register with a username and password!");
        String username = TERMINAL.readLine("Username: ");
        String password = TERMINAL.readLine("password: ");
        return new User(username, password);
    }

    /**
     * Registers or logs a user into the blackboard.
     */
    private void handleRegisterIfNecessary(@Nonnull final APIClient client,
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

    public boolean awaitHiringAnswer() {
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

    private void printToken(@Nonnull APIClient client) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : client.getTokenMap().entrySet()) {
            stringBuilder
                    .append(entry.getKey())
                    .append(" -> ")
                    .append(entry.getValue());
        }
        print(stringBuilder.toString());
    }

    public boolean acceptToNewHiring(String hiring) {
        print("Incoming Hiring:");
        print(hiring);
        return awaitHiringAnswer();
    }
}
