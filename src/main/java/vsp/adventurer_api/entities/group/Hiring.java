package vsp.adventurer_api.entities.group;

public class Hiring {

    /**
     * url to the created group at the taverna.
     */
    private String group;

    /**
     * the quest which shall be solved with the group.
     */
    private String quest;

    /**
     * something you want to tell the player you invite.
     */
    private String message;

    public Hiring(String group, String quest, String message) {
        this.group = group;
        this.quest = quest;
        this.message = message;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getQuest() {
        return quest;
    }

    public void setQuest(String quest) {
        this.quest = quest;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
