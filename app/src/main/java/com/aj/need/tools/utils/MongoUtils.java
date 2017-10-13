package com.aj.need.tools.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joan on 29/09/2017.
 */

public class MongoUtils {

    public static JSONObject currentDate(String key) throws JSONException {
        return __.jo().put(
                "$currentDate"
                , __.jo().put(key,true)
        );
    }
}
