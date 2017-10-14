package com.aj.need.db.colls;

import com.aj.need.db.colls.itf.Coll;
import com.google.firebase.firestore.CollectionReference;

/**
 * Created by joan on 02/10/2017.
 */

public final class USER_KEYWORDS implements Coll {

    private USER_KEYWORDS() {
    }

    private static String coll = "_KEYWORDS";

    public final static String activeKey = "active";
    public final static String keywordKey = "keyword";


    public final static CollectionReference getUserKeywordsRef(String uid) {
        return USERS.getUserRef(uid).collection(coll);
    }

    public final static CollectionReference getCurrentUserKeywordsRef() {
        return USERS.getCurrentUserRef().collection(coll);
    }

}
