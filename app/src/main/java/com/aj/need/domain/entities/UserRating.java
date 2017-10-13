package com.aj.need.domain.entities;

/**
 * Created by joan on 13/10/2017.
 */

public class UserRating {

    public final static String coll = "USER_RATINGS";
    public final static String ratingKey = "rating";

    private float rating;



    public UserRating() {
    }

    public UserRating(float rating) {
        this.rating = rating;
    }

    public float getRating() {
        return rating;
    }
}
