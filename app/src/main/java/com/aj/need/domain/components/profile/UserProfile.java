package com.aj.need.domain.components.profile;

import com.aj.need.db.colls.USERS;
import com.aj.need.domain.entities.Entity;
import com.aj.need.tools.utils.Avail;
import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by joan on 21/09/2017.
 */

//DB READ ONLY OBJECT
public class UserProfile extends Entity implements Serializable, ITranslatable<UserProfile> {

    private String _id;

    private String username;
    private int reputation;
    private int availability;


    public UserProfile() {
    }


    protected UserProfile(
            String _id
            , String username
            , int reputation
            , int availability
    ) {
        this._id = _id;

        this.username = username;
        this.reputation = reputation;
        this.availability = availability;
    }


    public String get_id() {
        return _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(Long reputation) {
        this.reputation = reputation != null ? reputation.intValue() : 0;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public boolean isIncomplete() {
        return getUsername() == null || getAvailability() == Avail.UNKNOWN;
    }


    @Override
    public String toString() {
        return username + " " + reputation + " " + availability + " " + date;
    }

    @Override
    public UserProfile tr(JSONObject json) {
        if (json == null) return null;

        String objectID = json.optString("objectID");
        String username = json.optString(USERS.usernameKey);
        int availability = json.optInt(USERS.availabilityKey, Avail.UNKNOWN);
        int rating = json.optInt("rating", 0); //// TODO: 19/11/2017 change to avgRating

        if (objectID != null && username != null && availability >= 0)
            return new UserProfile(objectID, username, rating, availability);

        return null;
    }


    @Override
    public UserProfile tr(DocumentSnapshot profile) {
        return null;
    }
}
