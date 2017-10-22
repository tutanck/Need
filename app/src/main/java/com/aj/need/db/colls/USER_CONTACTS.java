package com.aj.need.db.colls;

import com.aj.need.db.colls.itf.Coll;
import com.google.firebase.firestore.CollectionReference;

/**
 * Created by joan on 02/10/2017.
 */

public final class USER_CONTACTS implements Coll {

    private USER_CONTACTS() {
    }

    private final static String coll = "_CONTACTS";


    public final static CollectionReference getUserContactsRef(String uid) {
        return USERS.getUserRef(uid).collection(coll);
    }

    public final static CollectionReference getCurrentUserContactsRef() {
        return USERS.getCurrentUserRef().collection(coll);
    }

}
