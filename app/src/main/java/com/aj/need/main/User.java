package com.aj.need.main;

import com.aj.need.domain.entities.Entity;
import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by joan on 13/10/2017.
 */

public class User extends Entity implements Serializable, ITranslatable<User> {

    //// TODO: 06/11/2017 test with new user creation
    private String instanceIDToken = FirebaseInstanceId.getInstance().getToken();
    private String username;
    private int availability;

    public User() {
    }

    User(String username, int availability) {
        this.username = username;
        this.availability = availability;
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

    @Override
    public User tr(JSONObject json) {
        return null;
    }

    @Override
    public User tr(DocumentSnapshot documentSnapshot) {
        return null;
    }
}
