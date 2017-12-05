package vsp.adventurer_api.entities.cache;

import vsp.adventurer_api.entities.Election;
import vsp.adventurer_api.entities.Message;
import vsp.adventurer_api.entities.assignment.Assignment;
import vsp.adventurer_api.entities.assignment.TaskResult;
import vsp.adventurer_api.entities.group.Group;
import vsp.adventurer_api.entities.group.Hiring;
import vsp.adventurer_api.http.api.OurRoutes;

public class Cache {
    public static WebResourceEntityCache<Group> GROUPS = new WebResourceEntityCache<>(Group.class, OurRoutes.GROUP);
    public static WebResourceEntityCache<Hiring> HIRINGS = new WebResourceEntityCache<>(Hiring.class, OurRoutes.HIRINGS);
    public static WebResourceEntityCache<Assignment> ASSIGNMENTS = new WebResourceEntityCache<>(Assignment.class, OurRoutes.ASSIGNMENTS);
    public static WebResourceEntityCache<Message> MESSAGES = new WebResourceEntityCache<>(Message.class, OurRoutes.MESSAGES);
    public static WebResourceEntityCache<Election> ELECTIONS = new WebResourceEntityCache<>(Election.class, OurRoutes.ELECTION);
    public static WebResourceEntityCache<TaskResult> RESULTS = new WebResourceEntityCache<>(TaskResult.class, OurRoutes.RESULTS);
}
