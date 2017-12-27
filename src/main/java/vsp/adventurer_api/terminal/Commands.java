package vsp.adventurer_api.terminal;

import static vsp.Application.print;

final class Commands {

    private static final String HELP = "!help";
    static final String WHOAMI = "!whoami";
    static final String QUESTS = "!quests";
    static final String MAP = "!map";
    static final String DELIVER = "!deliver";
    static final String DELIVERIES = DELIVER + "ies";
    static final String TASK = "!task";
    static final String QUIT = "!quit";
    static final String GET = "!get";
    static final String POST = "!post";
    static final String PUT = "!put";
    static final String HOST = "!host";
    static final String QUEST = "!quest";
    static final String NEW_TOKEN = "!newToken";
    static final String TOKEN = "!token";
    static final String SET_TOKEN = "!setToken";
    static final String VISITS = "!visits";
    static final String HIRING = "!hiring";
    static final String GROUP = "!group";
    static final String ASSIGN = "!assign";
    static final String MEMBER = "!member";
    static final String ASSIGNMENTS = "!assignments";
    static final String RESULT = "!result";
    static final String RESULTS = "!results";
    static final String PARTICIPANTS = "!participants";
    static final String ELECTION = "!election";
    static final String MUTEX = "!mutex";
    static final String LEAVE_MUTEX = "!leaveMutex";
    static final String PREPARE_MUTEX = "!prepare";

    private Commands() {
    }

    static void showHelpMessage() {
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
                "# Mutex\n" +
                PREPARE_MUTEX + " - saves other services \n" +
                MUTEX + " - tries to enter critical zone \n" +
                LEAVE_MUTEX + " - releases critical zone \n" +
                "# Debug commands: \n" +
                GET + " <path> - GET on given path \n" +
                POST + " <path> <body> - POST with given path and body \n" +
                PUT + " <path> <body> - POST with given path and body \n"
        );
    }
}
