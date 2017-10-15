package com.aj.need.db.colls;

import com.aj.need.db.colls.itf.Coll;

import com.google.firebase.firestore.CollectionReference;

/**
 * Created by joan on 02/10/2017.
 */

public final class USER_NEEDS implements Coll {

    private USER_NEEDS() {
    }

    private static String coll = "_NEEDS";
    public final static String activeKey = "active";
    public final static String titleKey = "title";
    public final static String searchKey = "search";
    public final static String descriptionKey = "description";


    public final static CollectionReference getUserNeedsRef(String uid) {
        return USERS.getUserRef(uid).collection(coll);
    }

    public final static CollectionReference getCurrentUserNeedsRef() {
        return USERS.getCurrentUserRef().collection(coll);
    }


}
