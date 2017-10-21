package com.aj.need.tools.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joan on 21/10/2017.
 */

public class Jarvis<T> {

    public List<T> tr(JSONArray array, ITranslatable<T> translatable) {
        List<T> list = new ArrayList<T>();
        if (array != null)
            for (int i = 0; i < array.length(); ++i) {
                JSONObject json = array.optJSONObject(i);
                if (json == null) continue;

                T t = (T) translatable.tr(json);

                if (t == null) continue;
                list.add(t);
            }
        return list;
    }
}
