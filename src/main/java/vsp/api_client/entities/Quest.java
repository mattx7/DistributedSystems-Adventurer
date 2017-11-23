package vsp.api_client.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Quest {

    private final List<Integer> deliveries;

    private final String description;

    /**
     * <b>unused</b>
     */
    private final List<Object> followups;

    @NotNull
    private final Integer id;

    @NotNull
    private final String name;

    /**
     * <b>unused</b>
     */
    private final List<Object> prerequisites;

    @Nullable
    private final Object requirements;

    private final Integer reward;

    private final List<String> tasks;


    public Quest(List<Integer> deliveries,
                 String description,
                 List<Object> followups,
                 @NotNull Integer id,
                 @NotNull String name,
                 List<Object> prerequisites,
                 @Nullable Object requirements,
                 Integer reward,
                 List<String> tasks) {
        this.deliveries = deliveries;
        this.description = description;
        this.followups = followups;
        this.id = id;
        this.name = name;
        this.prerequisites = prerequisites;
        this.requirements = requirements;
        this.reward = reward;
        this.tasks = tasks;
    }

    public List<Integer> getDeliveries() {
        return deliveries;
    }

    public String getDescription() {
        return description;
    }

    public List<Object> getFollowups() {
        return followups;
    }

    @NotNull
    public Integer getId() {
        return id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public List<Object> getPrerequisites() {
        return prerequisites;
    }

    @Nullable
    public Object getRequirements() {
        return requirements;
    }

    public Integer getReward() {
        return reward;
    }

    public List<String> getTasks() {
        return tasks;
    }
}
