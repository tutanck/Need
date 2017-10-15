package com.aj.need.db.colls;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.domain.components.messages.Message;
import com.aj.need.tools.regina.Regina;

import com.aj.need.tools.regina.ack._Ack;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.WriteBatch;

import org.json.JSONException;

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


    public final static CollectionReference getMessagesRef() {
        return IO.db.collection(coll);
    }


    public static void loadMessages(String aID, String bID, _Ack ack) {
        try {
            IO.r.find(
                    coll
                    , __.jo().put(
                            "$or"
                            , __.jar()
                                    .put(__.jo().put(fromKey, aID).put(toKey, bID))
                                    .put(__.jo().put(toKey, aID).put(fromKey, bID))
                    )
                    , __.jo().put("sort", __.jo().put(dateKey, 1))
                    , __.jo(), ack
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


    public static Task<Void> sendMessage(String to, String text) {

        String uid = IO.auth.getCurrentUser().getUid();

        Message msg = new Message(text, uid, to, false);

        DocumentReference msgRef = getMessagesRef().document();
        DocumentReference senderUcRef = USER_CONTACTS.getCurrentUserContactsRef().document(to);
        DocumentReference recipientUcRef = USER_CONTACTS.getUserContactsRef(to).document(uid);

        WriteBatch batch = IO.db.batch();
        batch.set(msgRef, msg);
        batch.set(senderUcRef, msg);
        batch.set(recipientUcRef, msg);

        FieldValue timestamp = FieldValue.serverTimestamp();
        batch.update(msgRef, Coll.dateKey, timestamp);
        batch.update(senderUcRef, Coll.dateKey, timestamp);
        batch.update(recipientUcRef, Coll.dateKey, timestamp);

        return batch.commit();
    }

}
