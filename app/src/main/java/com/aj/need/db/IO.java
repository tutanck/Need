package com.aj.need.db;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

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
        //// TODO: 06/11/2017 : this .m must be the only one method used to access uid  
        //// TODO: 06/11/2017 test auth !=null && currentUser!=null && uid !null and else go to login page
        //// TODO: 06/11/2017  test after deleting all app infos
        return auth.getCurrentUser().getUid();
    }

    public static FirebaseUser getCurrentUser(){
        return auth.getCurrentUser();
    }

    public static String getInstanceIDToken() {
        return FirebaseInstanceId.getInstance().getToken();
    }

}
