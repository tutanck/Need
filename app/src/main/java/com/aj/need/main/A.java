package com.aj.need.main;

import android.app.Activity;
import android.app.Application;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.tools.utils.__;
import com.aj.need.tools.regina.Regina;
import com.aj.need.tools.regina.ack.UIAck;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class A extends Application {

    private String user_id = null;


    public static String user_id(Activity activity) {
        return ((A) activity.getApplication()).user_id;
    }
}