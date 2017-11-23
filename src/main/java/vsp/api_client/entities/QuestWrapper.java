package vsp.api_client.entities;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuestWrapper { // TODO generify

    @NotNull
    private final List<Quest> objects;

    @NotNull
    private final String status;

    public QuestWrapper(@NotNull List<Quest> objects, @NotNull String status) {
        this.objects = objects;
        this.status = status;
    }

    @NotNull
    public List<Quest> getObjects() {
        return objects;
    }

    @NotNull
    public String getStatus() {
        return status;
    }
}
