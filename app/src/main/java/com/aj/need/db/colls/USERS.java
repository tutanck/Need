package com.aj.need.db.colls;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

/**
 * Created by joan on 02/10/2017.
 */

public final class USERS implements Coll {

    private USERS() {
    }

    private final static String coll = "USERS";

    public final static String instanceIDTokenKey = "instanceIDToken";

    public final static String typeKey = "type";
    public final static String usernameKey = "username";
    public final static String resumeKey = "resume";
    public final static String tariffKey = "tariff";
    public final static String localityKey = "locality";
    public final static String metaLocationCoordKey = "metaLocationCoord";


    public final static String availabilityKey = "availability";
    public final static String avgRatingKey = "avgRating";
    public final static String nbVotersKey = "nbVoters";


    public final static DocumentReference getUserRef(String id) {
        return getColl().document(id);
    }

    public final static DocumentReference getCurrentUserRef() {
        return getUserRef(IO.getCurrentUserUid());
    }

    public final static CollectionReference getColl() {
        return IO.db.collection(coll);
    }

}
