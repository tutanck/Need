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


    public static void computeUserContacts(String userID, _Ack ack) {
        try {
            IO.socket.emit("getUserContacts", __.jo().put("userID", userID), null, ack);
        } catch (JSONException e) {
            __.fatal(e);
        }
    }

}
