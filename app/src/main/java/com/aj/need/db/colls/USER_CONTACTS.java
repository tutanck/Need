package com.aj.need.db.colls;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.tools.regina.ack._Ack;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.aj.need.db.IO.db;

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


    public static Task<List<UserProfile>> computeCurrentUserContacts() {

        return db.runTransaction(new Transaction.Function<List<UserProfile>>() {
            @Override
            public List<UserProfile> apply(final Transaction transaction) throws FirebaseFirestoreException {

                final List<UserProfile> contacts = new ArrayList<UserProfile>();

                USER_CONTACTS.getCurrentUserContactsRef().get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {

                                    for (final DocumentSnapshot contactDoc : task.getResult()) {
                                        Log.d("22LOL22TR", contactDoc.getId() + " => " + contactDoc.getData());

                                        new AsyncTask<Object, Integer, Long>(){

                                            @Override
                                            protected Long doInBackground(Object[] objects) {
                                                try {
                                                    DocumentSnapshot userDoc = transaction.get(USERS.getUserRef(contactDoc.getId()));
                                                    if(userDoc!= null && userDoc.exists())
                                                        contacts.add(new UserProfile(
                                                                        userDoc.getId()
                                                                        , userDoc.getString(USERS.usernameKey)
                                                                        ,0 //// TODO: 15/10/2017
                                                                        ,userDoc.getLong(USERS.availabilityKey).intValue()
                                                                        ,contactDoc.getString(MESSAGES.messageKey)
                                                                        ,contactDoc.getDate(MESSAGES.dateKey).toString()
                                                                )
                                                        );

                                                } catch (FirebaseFirestoreException e) {
                                                    e.printStackTrace();
                                                    //__.fatal("TR Failed : computeUserContacts : FirebaseFirestoreException");
                                                }
                                                return null;
                                            }
                                        }.execute();




                                    }

                                } else {
                                    __.fatal("TR Failed : computeUserContacts"); //// TODO: 15/10/2017 urgent important!
                                    //// TODO: 15/10/2017
                                }
                            }
                        });

                return contacts;
            }
        });



        /*try {
            IO.socket.emit("getUserContacts", __.jo().put("userID", userID), null, ack);
        } catch (JSONException e) {
            __.fatal(e);
        }*/
    }




    private static  class BackTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            long totalSize = 0;

            return totalSize;
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {

        }
    }


}
