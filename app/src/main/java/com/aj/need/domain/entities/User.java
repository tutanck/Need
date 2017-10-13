package com.aj.need.domain.entities;

/**
 * Created by joan on 13/10/2017.
 */

public class User {

    public final static String coll = "USERS";
    public final static String typeKey = "type";
    public final static String usernameKey = "username";
    public final static String availabilityKey = "availability";

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
