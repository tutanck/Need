package com.aj.need.domain.entities;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/**
 * Created by joan on 26/10/2017.
 */

public class Entity {

    @ServerTimestamp
    protected Date date;

    public Date getDate() {
        return date;
    }
}
