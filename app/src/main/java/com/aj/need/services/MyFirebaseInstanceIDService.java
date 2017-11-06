package com.aj.need.services;

import android.util.Log;

import com.aj.need.db.IO;
import com.aj.need.db.colls.USERS;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = IO.getInstanceIDToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        //nothing to do : here (unstable state that can lead to app crash when trying to use user's uid (from Firebase auth))
        // // TODO: 06/11/2017  make sure we go instead  to the login page @see  https://firebase.google.com/docs/cloud-messaging/android/client

        /*The registration token may change when:
        The app deletes Instance ID
        The app is restored on a new device
        The user uninstalls/reinstall the app
        The user clears app data.**/
    }
}
