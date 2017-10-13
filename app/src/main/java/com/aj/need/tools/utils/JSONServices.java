package com.aj.need.tools.utils;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by joan on 23/09/2017.
 */

public class JSONServices {

    public static JSONObject loadJsonFromAsset(String filename, Context context) {
        try {
            InputStream is = context.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new JSONObject(new String(buffer, "UTF-8"));
        } catch (java.io.IOException |JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
