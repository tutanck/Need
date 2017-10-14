package com.aj.need.db.colls;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.tools.regina.ack._Ack;
import com.aj.need.tools.utils.__;

import com.google.firebase.firestore.CollectionReference;

import org.json.JSONException;

/**
 * Created by joan on 02/10/2017.
 */

public final class USER_RATINGS implements Coll {

    private USER_RATINGS() {
    }

    private final static String coll = "_RATINGS";
    public final static String ratingKey = "rating";


    public final static CollectionReference getUserRatingsRef(String uid) {
        return USERS.getUserRef(uid).collection(coll);
    }

    public final static CollectionReference getCurrentUserRatingsRef() {
        return USERS.getCurrentUserRef().collection(coll);
    }

}
