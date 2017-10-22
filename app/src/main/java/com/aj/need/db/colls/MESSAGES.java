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

    public static void sendMessage(
            String to
            , String text
            , String conversationID
            , final OnSuccessListener<Void> onSuccessListener
            , final OnFailureListener onFailureListener
    ) {

        String uid = IO.auth.getCurrentUser().getUid();

        if (conversationID == null) {
            conversationID = uid + "_" + to;
            Log.i("conversationID:", "new conversationID : " + conversationID);
        }

        Message msg = new Message(text, uid, to, conversationID, false);

        final DocumentReference msgRef = getMESSAGESRef().document();
        final DocumentReference senderUcRef = USER_CONTACTS.getCurrentUserContactsRef().document(to);
        final DocumentReference recipientUcRef = USER_CONTACTS.getUserContactsRef(to).document(uid);

        final WriteBatch batch = IO.db.batch();
        batch.set(msgRef, msg);
        batch.set(senderUcRef, msg);
        batch.set(recipientUcRef, msg);

        batch.commit()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                            Log.d("NOPe", "succ");
                        else
                            Log.d("NOPe", "fail");
                    }
                })
                .addOnFailureListener(onFailureListener)
                .addOnSuccessListener(onSuccessListener)


        /* todo
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        WriteBatch batch = IO.db.batch();
                        FieldValue timestamp = FieldValue.serverTimestamp();
                        batch.update(msgRef, Coll.dateKey, timestamp);
                        batch.update(senderUcRef, Coll.dateKey, timestamp);
                        batch.update(recipientUcRef, Coll.dateKey, timestamp);
                        batch.commit()
                                .addOnFailureListener(onFailureListener)
                                .addOnSuccessListener(onSuccessListener);
                    }
                })
                */;

    }
}