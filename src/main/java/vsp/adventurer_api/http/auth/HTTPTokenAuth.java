package vsp.adventurer_api.http.auth;

import com.google.common.base.Preconditions;
import vsp.adventurer_api.entities.basic.Token;
import vsp.adventurer_api.entities.basic.User;

import javax.annotation.Nonnull;

public class HTTPTokenAuth implements HTTPAuthentication {

    @Nonnull
    private final Token token;

    public HTTPTokenAuth(@Nonnull Token token) {
        Preconditions.checkNotNull(token, "token must not be null.");

        this.token = token;
    }

    public static HTTPTokenAuth forUser(@Nonnull final User user) {
        Preconditions.checkNotNull(user, "user must not be null.");

        return new HTTPTokenAuth(user.getToken()); // TODO Exc
    }

    @Nonnull
    public String getAuthHeader() {
        return "Token " + token.getToken();
    }

    @Override
    public String getDebugInfo() {
        return token.getToken();
    }
}
