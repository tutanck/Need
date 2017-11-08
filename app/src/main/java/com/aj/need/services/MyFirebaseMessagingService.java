package com.aj.need.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aj.need.R;
import com.aj.need.domain.components.messages.MessagesActivity;
import com.aj.need.domain.components.needs.UserNeedActivity;
import com.aj.need.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final int MyFirebaseMessagingServiceRequestCode = 0;
    private static int notificationID = 0;
    private static final String USER_NEED_ACTIVITY = ".domain.components.needs.UserNeedActivity";
    private static final String MESSAGES_ACTIVITY = ".domain.components.messages.MessagesActivity";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow(remoteMessage);
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

       // sendNotification(remoteMessage); //not trusted : // TODO: 07/11/2017  redo #later
        //todo https://developer.android.com/guide/topics/ui/notifiers/notifications.html
        //todo https://github.com/firebase/quickstart-android/tree/master/messaging/app/src/main/java/com/google/firebase/quickstart/fcm
    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        Log.d(TAG, "Long lived task is done.");
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow(RemoteMessage remoteMessage) {
        Log.d(TAG, "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param remoteMessage FCM message body received.
     */
    private void sendNotification(RemoteMessage remoteMessage) {

        Intent intent = selectIntent(remoteMessage);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this
                , MyFirebaseMessagingServiceRequestCode /* Request code */
                , intent,
                PendingIntent.FLAG_ONE_SHOT
        );

        String channelId = getString(R.string.default_notification_channel_id);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_person_24dp) //// TODO: 07/11/2017 app icon
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(remoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(getNotificationID()/* ID of notification */, notificationBuilder.build());
    }


    private Intent selectIntent(RemoteMessage remoteMessage) {
        String clickActionURI = remoteMessage.getNotification().getClickAction();
        if (clickActionURI != null)
            switch (clickActionURI) {
                case USER_NEED_ACTIVITY:
                    Intent intent_u = new Intent(this, UserNeedActivity.class);
                   /* intent_u.putExtra(NEED_ID, remoteMessage.getData().get(NEED_ID));
                    intent_u.putExtra(NEED_TITLE, remoteMessage.getData().get(NEED_TITLE));
                    intent_u.putExtra(APPLICANT_ID, remoteMessage.getData().get(APPLICANT_ID));
                    intent_u.putExtra(APPLICANT_NAME, remoteMessage.getData().get(APPLICANT_NAME));*/
                    return intent_u;
                case MESSAGES_ACTIVITY:
                    Intent intent_m = new Intent(this, MessagesActivity.class);
                    return intent_m;
                default:
                    return new Intent(this, MainActivity.class);
            }

        return new Intent(this, MainActivity.class);
    }


    private int getNotificationID() {
        return ((notificationID++) % 16); //Maximum 16 notifications at a time
    }
}
