package com.aj.need.main;

import com.aj.need.domain.entities.Entity;
import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by joan on 13/10/2017.
 */

public class User extends Entity implements Serializable, ITranslatable<User> {

    private String username;
    private int availability;

    public User() {
    }

    User(String username, int availability) {
        this.username = username;
        this.availability = availability;
    }

    public String getUsername() {
        return username;
    }

    public int getAvailability() {
        return availability;
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
