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
    public final static String ownerIDKey = "ownerID";
    public final static String ownerNameKey = "ownerName";
    public final static String titleKey = "title";
    public final static String searchKey = "search";
    public final static String descriptionKey = "description";
    public final static String whereKey = "where";
    public final static String rewardKey = "reward";
    public final static String metaWhereCoordKey = "metaWhereCoord";
    public final static String metaIsWhereVisibleKey = "metaIsWhereVisible";


    public final static CollectionReference getUserNeedsRef(String uid) {
        return USERS.getUserRef(uid).collection(coll);
    }

    public final static CollectionReference getCurrentUserNeedsRef() {
        return USERS.getCurrentUserRef().collection(coll);
    }


}
