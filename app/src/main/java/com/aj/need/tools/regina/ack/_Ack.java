package com.aj.need.tools.regina.ack;

import android.app.Activity;
import android.util.Log;

import com.aj.need.tools.utils.__;

import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Ack;

/**
 * Created by joan on 24/09/2017.
 */

public abstract class _Ack implements Ack {

    protected final Activity activity;
    protected boolean isDebugOn = true;


    protected abstract void onRes(Object res, JSONObject ctx);

    protected void onErr(
            JSONObject err,
            JSONObject ctx
    ) {
        __.showShortToast(activity, "Une erreur s'est produite");
        Log.e("_Ack/onErr","err=##"+err+"## ctx="+ctx);
    }


    protected void onReginaFail(
    ) {
        __.showShortToast(activity, "Une erreur s'est produite!");//note the exclamation mark : !
        Log.e("_Ack/onReginaFail","Regina Failed");
    }


    public _Ack(
            Activity activity
    ) {
        this.activity = activity;
    }

    public _Ack(
            Activity activity
            , boolean isDebugOn
    ) {
        this.activity = activity;
        this.isDebugOn = isDebugOn;
    }


    protected final void logObjectList(Object... objects) {
        ArrayList<String> strList = new ArrayList<>();
        for (Object obj : objects) strList.add("" + obj); //.toString() here could NPE
        Log.i("@logObjectList", strList.toString());
    }

}


