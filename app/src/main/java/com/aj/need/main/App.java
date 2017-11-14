package com.aj.need.main;

import android.app.Application;
import android.location.Location;
import android.net.Uri;

import com.aj.need.tools.utils.Coord;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 12;




    /*Current user*/

    private User user; //light image of the remote user

    public User getUser() {
        return user;
    }

    public void updateUser(User user) {
        this.user = user;
    }

    public String getUserName() {
        return this.user.getUsername();
    }




    /*imageUriMap*/

    private Map<String, Uri> imageUriMap = new HashMap<>();

    public synchronized void setImageUri(String id, Uri uri) {
        imageUriMap.put(id, uri);
    }

    public Uri getImageUri(String id) {
        return imageUriMap.get(id);
    }




    /*lastLocalKnownLocation*/

    private Location lastLocalKnownLocation;

    public Location getLastLocalKnownLocation() {
        return lastLocalKnownLocation;
    }

    public void setLastLocalKnownLocation(Location lastLocalKnownLocation) {
        this.lastLocalKnownLocation = lastLocalKnownLocation;
    }
}