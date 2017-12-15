package vsp.adventurer_api.entities.basic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Date;

public class Token {
    @Nonnull
    private final String token;

    @Nullable
    private final Date date; // TODO test if this is set after json conversion

    public Token(@Nonnull final String token, @Nullable final Date date) {
        this.token = token;
        this.date = date;
    }

    @Nonnull
    public String getToken() {
        return token;
    }

    @Nullable
    public Date getDate() {
        return date;
    }
}