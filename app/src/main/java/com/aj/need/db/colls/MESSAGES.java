package com.aj.need.db.colls;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.google.firebase.firestore.CollectionReference;

/**
 * Created by joan on 02/10/2017.
 */

public final class MESSAGES implements Coll {

    private MESSAGES() {
    }


    private final static String coll = "MESSAGES";

    public final static String fromKey = "from";
    public final static String toKey = "to";
    public final static String messageKey = "message";
    public final static String conversationIDKey = "conversationID";
    public final static String messageIDKey = "messageID";
    public final static String lastReadKey = "lastRead";
    public final static String readKey = "read";


    public final static CollectionReference getMESSAGESRef() {
        return IO.db.collection(coll);
    }


}