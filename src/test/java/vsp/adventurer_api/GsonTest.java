package vsp.adventurer_api;

import com.google.gson.Gson;
import org.testng.annotations.Test;
import vsp.adventurer_api.election.ElectionParticipant;

import static org.testng.Assert.assertEquals;

public class GsonTest {

    @Test
    public void test() throws Exception {
        ElectionParticipant[] participants = new ElectionParticipant[]{
                new ElectionParticipant("ip", 1),
                new ElectionParticipant("ip2", 2)};

        Gson testObject = new Gson();
        String json = testObject.toJson(participants);
        System.out.println(json);

        ElectionParticipant[] createdEP = testObject.fromJson(json, ElectionParticipant[].class);

        assertEquals(createdEP, participants);
    }

}
