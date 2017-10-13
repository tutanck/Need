package com.aj.need.tools.regina.ack;

import android.app.Activity;

import org.json.JSONObject;

/**
 * Created by joan on 24/09/2017.
 */

public abstract class UIAck extends _Ack {

    @Override
    public final void call(final Object... args) {
        if (isDebugOn)
            logObjectList(args);
        activity.runOnUiThread(new Runnable() { //mandatory to modify an activity's ui view
            @Override
            public void run() {
                if (args[0] != null && args[2]!=null)
                    onErr((JSONObject) args[0], (JSONObject) args[2]);
                else if (args[1] != null && args[2]!=null)
                    onRes(args[1], (JSONObject) args[2]);
                else
                    onReginaFail();
            }
        });
    }


    public UIAck(Activity activity) {
        super(activity);
    }

    public UIAck(Activity activity, boolean isDebugOn) {
        super(activity, isDebugOn);
    }


}


