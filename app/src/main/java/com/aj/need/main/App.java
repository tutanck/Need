package com.aj.need.main;

import android.app.Application;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    private User user;

    public User getUser() {
        return user;
    }

    public String getUserName() {
        return this.user.getUsername();
    }

    public int getUserAvailability() {
        return this.user.getAvailability();
    }

    public void updateUser(User user) {
        this.user = user;
    }

    private Map<String, Uri> imageUriMap = new HashMap<>();

    public void setImageUri(String id, Uri uri) {
        imageUriMap.put(id,uri);
    }

    public Uri getImageUri(String id) {
        return imageUriMap.get(id);
    }
}