package vsp.api_client.entities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Representation of a user from the rest api.
 */
public class User {

    @NotNull
    private String name;

    @NotNull
    private String password;

    @Nullable
    private Token token;

    public User(@NotNull String name, @NotNull String password) {
        this.name = name;
        this.password = password;
    }

    public void setToken(@Nullable Token token) {
        this.token = token;
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public String getPassword() {
        return password;
    }

    @Nullable
    public Token getToken() {
        return token;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return name.equals(user.name) && password.equals(user.password);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}
