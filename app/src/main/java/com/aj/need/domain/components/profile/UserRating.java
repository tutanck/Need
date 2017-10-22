package com.aj.need.domain.components.profile;

import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by joan on 13/10/2017.
 */

public class UserRating implements Serializable, ITranslatable<UserRating> {

    private float rating;

    public UserRating() {
    }

    UserRating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }

    @Override
    public UserRating tr(JSONObject json) {
        return null;
    }

    @Override
    public UserRating tr(DocumentSnapshot documentSnapshot) {
        return null;
    }
}
