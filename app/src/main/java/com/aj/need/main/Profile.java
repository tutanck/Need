package com.aj.need.main;

/**
 * Created by joan on 13/10/2017.
 */

public class Profile {

    public final static String coll = "PROFILES";

    private String username;
    private int availability;

    public Profile() {
    }

    public Profile(String username, int availability) {
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
