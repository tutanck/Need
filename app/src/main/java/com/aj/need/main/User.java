package com.aj.need.main;

import android.location.Location;

import com.aj.need.domain.entities.Entity;
import com.aj.need.tools.utils.Coord;
import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by joan on 13/10/2017.
 */

public class User extends Entity implements Serializable, ITranslatable<User> {

    private String instanceIDToken = FirebaseInstanceId.getInstance().getToken();
    private String username;
    private int availability;
    private Coord location;


    public User() {
    }

    User(String username, int availability, Coord location) {
        this.username = username;
        this.availability = availability;
        this.location = location;
    }

    User(String username, int availability, Location loc) {
        this(username, availability, loc == null ? null : new Coord(loc.getLatitude(), loc.getLongitude()));
    }


    public String getInstanceIDToken() {
        return instanceIDToken;
    }

    public String getUsername() {
        return username;
    }

    public int getAvailability() {
        return availability;
    }

    public Coord getLocation() {
        return location;
    }

    @Override
    public User tr(JSONObject json) {
        return null;
    }

    @Override
    public User tr(DocumentSnapshot documentSnapshot) {
        return null;
    }
}
