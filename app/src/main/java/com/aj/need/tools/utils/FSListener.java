package com.aj.need.tools.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.aj.need.R;
import com.google.android.gms.tasks.OnFailureListener;

/**
 * Created by joan on 26/10/2017.
 */

public class FSListener {

    public static OnFailureListener makeFL(
            final Activity activity
            , final String logMessage
    ) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onErr(activity, logMessage, e);
            }
        };
    }


    public static OnFailureListener makeFL(
            final Activity activity
            , final SwipeRefreshLayout swipeRefreshLayout
            , final String logMessage
    ) {
        return new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onErr(activity, logMessage, e);
                swipeRefreshLayout.setRefreshing(false);
            }
        };
    }


    private static void onErr(
            Activity activity,
            String logMessage,
            @NonNull Exception e
    ) {
        //!important : useful log for index issues tracking, etc.
        Log.d("FSListener/guard", logMessage, e);
        __.showShortToast(activity, activity.getString(R.string.error_message));
    }
}
