package com.example.goodlink.core.domain.model.user;

import androidx.annotation.NonNull;

import java.util.Objects;

public final class User {

    private final String id;
    private final String name;
    private final String email;

    public User(String id, String name, String email) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id.equals(user.id) &&
                name.equals(user.name) &&
                email.equals(user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email);
    }

    @NonNull
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}