package vsp;

import com.google.gson.Gson;
import vsp.adventurer_api.entities.group.GroupWrapper;

public class Tests {
    public static void main(String[] args) {
        final Gson jsonConverter = new Gson();

        final String json =
                "{\"_links\": {\"members\": \"/taverna/groups/314/members\", \"self\": \"/taverna/groups/314\"}, " +
                        "\"id\": 314, " +
                        "\"members\": [\"L\", \"K\", \"Z\", \"Keil1\", \"joko\"], " +
                        "\"owner\": \"K\"}";

        final GroupWrapper result = jsonConverter.fromJson(json, GroupWrapper.class);

        System.out.printf(result.toString());
    }
}
