package vsp.adventurer_api.entities.basic;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Representation of a user from the rest api.
 */
public class User {

    @Nonnull
    private String name;

    @Nonnull
    private String password;

    @Nullable
    private Token token;

    public User(@Nonnull String name, @Nonnull String password) {
        this.name = name;
        this.password = password;
    }

    public void setToken(@Nullable Token token) {
        this.token = token;
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
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