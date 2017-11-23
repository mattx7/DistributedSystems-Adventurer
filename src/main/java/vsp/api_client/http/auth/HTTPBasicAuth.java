package vsp.api_client.http.auth;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import vsp.api_client.entities.User;

import java.util.Base64;

/**
 * Representation of a basic authentication for
 */
public class HTTPBasicAuth implements HTTPAuthentication {

    @NotNull
    private final String username;

    @NotNull
    private final String password;


    public HTTPBasicAuth(@NotNull String username, @NotNull String password) {
        Preconditions.checkNotNull(username, "username must not be null.");
        Preconditions.checkNotNull(password, "password must not be null.");

        this.username = username;
        this.password = password;
    }

    /**
     * Returns a basic auth with the name an password from the given user.
     *
     * @param user Not null.
     * @return New Instance of this class.
     */
    public static HTTPBasicAuth forUser(@NotNull final User user) {
        Preconditions.checkNotNull(user, "user must not be null.");

        return new HTTPBasicAuth(user.getName(), user.getPassword());
    }

    public String getAsString() {
        return username + ":" + password;
    }

    public String getAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(getAsString().getBytes());
    }

    @Override
    public String getDebugInfo() {
        return getAsString();
    }
}
