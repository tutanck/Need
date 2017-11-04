package com.aj.need.db.colls;

import com.aj.need.db.colls.itf.Coll;
import com.google.firebase.firestore.CollectionReference;

/**
 * Created by joan on 02/10/2017.
 */

public final class APPLICANTS implements Coll {

    private APPLICANTS() {
    }

    private static String coll = "_APPLICANTS";

    public final static String activeKey = "active";
    public final static String applicantIDKey = "applicantID";

    public final static CollectionReference getAdApplicantsRef(String ownerID, String _id) {
        return USER_NEEDS.getUserNeedsRef(ownerID).document(_id).collection(coll);
    }

}
