package com.aj.need.db.colls;

import android.support.annotation.NonNull;
import android.util.Log;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.domain.components.messages.Message;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.WriteBatch;


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
    public final static String openKey = "open";


    public final static CollectionReference getMESSAGESRef() {
        return IO.db.collection(coll);
    }


}