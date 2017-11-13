package com.aj.need.db.colls;

import android.util.Log;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;

import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.tools.utils.__;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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

    /*public static Task<List<UserProfile>> computeUsersInfo(final QuerySnapshot result) {

        return db.runTransaction(new Transaction.Function<List<UserProfile>>() {
            @Override
            public List<UserProfile> apply(final Transaction transaction) throws FirebaseFirestoreException {

                final List<UserProfile> contacts = new ArrayList<>();

                for (DocumentSnapshot contactDoc : result)
                    try {
                        Log.d("computeUsersInfo", contactDoc.getId() + " => " + contactDoc.getData());

                        DocumentReference userRef = USERS.getUserRef(contactDoc.getId());
                        DocumentSnapshot userDoc = transaction.get(userRef);

                        //!important : each doc read in a transaction must be also wrote
                        transaction.update(userRef, "lastRead", FieldValue.serverTimestamp());

                        contacts.add(new UserProfile(
                                        userDoc.getId()
                                        , userDoc.getString(USERS.usernameKey)
                                        , userDoc.getLong(USERS.avgRatingKey).intValue()
                                        , userDoc.getLong(USERS.availabilityKey).intValue()
                                        , contactDoc.getString(MESSAGES.conversationIDKey)
                                        , contactDoc.getString(MESSAGES.messageKey)
                                        , contactDoc.getDate(MESSAGES.dateKey)
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
    }*/

}
