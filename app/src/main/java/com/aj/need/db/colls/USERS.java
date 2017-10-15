package com.aj.need.db.colls;

import android.util.Log;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;

import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.List;

import static com.aj.need.db.IO.db;

/**
 * Created by joan on 02/10/2017.
 */

public final class USERS implements Coll {

    private USERS() {
    }

    private final static String coll = "USERS";

    public final static String authIDKey = "authID";
    public final static String typeKey = "type";
    public final static String usernameKey = "username";
    public final static String availabilityKey = "availability";

    public final static DocumentReference getUserRef(String id) {
        return IO.db.collection(coll).document(id);
    }

    public final static DocumentReference getCurrentUserRef() {
        return getUserRef(IO.auth.getCurrentUser().getUid());
    }


    public static Task<List<UserProfile>> computeUsersInfo(final QuerySnapshot result) {

        return db.runTransaction(new Transaction.Function<List<UserProfile>>() {
            @Override
            public List<UserProfile> apply(final Transaction transaction) throws FirebaseFirestoreException {

                final List<UserProfile> contacts = new ArrayList<>();

                for (DocumentSnapshot contactDoc : result)
                    try {
                        Log.d("22LOL22TR", contactDoc.getId() + " => " + contactDoc.getData());

                        DocumentReference userRef = USERS.getUserRef(contactDoc.getId());

                        DocumentSnapshot userDoc = transaction.get(userRef);

                        //!important : each doc read in a transaction must be also wrote
                        transaction.update(userRef, "lastRead", FieldValue.serverTimestamp());

                        contacts.add(new UserProfile(
                                        userDoc.getId()
                                        , userDoc.getString(USERS.usernameKey)
                                        , 0 //// TODO: 15/10/2017
                                        , userDoc.getLong(USERS.availabilityKey).intValue()
                                        , contactDoc.getString(MESSAGES.conversationIDKey)
                                        , contactDoc.getString(MESSAGES.messageKey)
                                        , ""+contactDoc.getDate(MESSAGES.dateKey)/*.toString()*/ //// TODO: 15/10/2017
                                )
                        );

                    } catch (FirebaseFirestoreException e) {
                        e.printStackTrace();
                        __.fatal("TR Failed : computeUserContacts : FirebaseFirestoreException");//// TODO: 15/10/2017 !important !urgent smooth stop then present retry to user
                        //// TODO: 15/10/2017
                    }

                return contacts;
            }
        });
    }


}
