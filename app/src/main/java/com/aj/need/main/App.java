package com.aj.need.main;

import android.app.Application;
import android.location.Location;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;


    private User user;

    private Map<String, Uri> imageUriMap = new HashMap<>();

    private Location location;


    public User getUser() {
        return user;
    }

    public void updateUser(User user) {
        this.user = user;
    }


    public String getUserName() {
        return this.user.getUsername();
    }

    public int getUserAvailability() {
        return this.user.getAvailability();
    }


    public void setImageUri(String id, Uri uri) {
        imageUriMap.put(id, uri);
    }

    public Uri getImageUri(String id) {
        return imageUriMap.get(id);
    }


    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}