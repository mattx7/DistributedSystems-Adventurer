package vsp.api_client.http.auth;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import vsp.api_client.entities.Token;
import vsp.api_client.entities.User;

public class HTTPTokenAuth implements HTTPAuthentication {

    @NotNull
    private final Token token;

    public HTTPTokenAuth(@NotNull Token token) {
        Preconditions.checkNotNull(token, "token must not be null.");

        this.token = token;
    }

    public static HTTPTokenAuth forUser(@NotNull final User user) {
        Preconditions.checkNotNull(user, "user must not be null.");

        return new HTTPTokenAuth(user.getToken()); // TODO Exc
    }

    @NotNull
    public String getAuthHeader() {
        return "Token " + token.getToken();
    }

    @Override
    public String getDebugInfo() {
        return token.getToken();
    }
}
