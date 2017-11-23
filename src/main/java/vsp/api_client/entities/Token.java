package vsp.api_client.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;

public class Token {

    @NotNull
    private final String token;

    @Nullable
    private final Date date; // TODO test if this is set after json conversion


    public Token(@NotNull final String token,
                 @Nullable final Date date) {
        this.token = token;
        this.date = date;
    }

    @NotNull
    public String getToken() {
        return token;
    }

    @Nullable
    public Date getDate() {
        return date;
    }
}
