package com.aj.need.db.colls;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;

import com.google.firebase.firestore.DocumentReference;

/**
 * Created by joan on 02/10/2017.
 */

public final class USERS implements Coll {

    private USERS() {
    }

    private final static String coll = "USERS";

    public final static String authIDKey = "authID";
    public final static String typeKey = "type";
    public final static String usernameKey = "username";
    public final static String availabilityKey = "availability";

    public final static DocumentReference getUserRef(String id) {
        return IO.db.collection(coll).document(id);
    }

    public final static DocumentReference getCurrentUserRef() {
        return getUserRef(IO.auth.getCurrentUser().getUid());
    }

}
