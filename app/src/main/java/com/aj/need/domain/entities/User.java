package com.aj.need.domain.entities;

/**
 * Created by joan on 13/10/2017.
 */

public class User {

    private String username;
    private int availability;

    public User() {
    }

    public User(String username, int availability) {
        this.username = username;
        this.availability = availability;
    }

    public String getUsername() {
        return username;
    }

    public int getAvailability() {
        return availability;
    }
}
