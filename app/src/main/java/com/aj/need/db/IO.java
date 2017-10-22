package com.aj.need.db;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by joan on 18/09/2017.
 */

public class IO {

    private IO() {
    }


    public static FirebaseAuth auth;
    public static FirebaseFirestore db;

    static {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static boolean isCurrentUser(String uid) {
        return auth.getCurrentUser().getUid().equals(uid);
    }

    public static String getCurrentUserUid() {
        return auth.getCurrentUser().getUid();
    }

}
