package com.aj.need.tools.regina.ack;

import android.app.Activity;

import org.json.JSONObject;

/**
 * Created by joan on 24/09/2017.
 */

public class VoidBAck extends BAck {


    public VoidBAck(Activity activity) {
        super(activity);
    }

    public VoidBAck(Activity activity, boolean isDebugOn) {
        super(activity, isDebugOn);
    }

    @Override
    protected void onRes(Object res, JSONObject ctx) {
    }

}


