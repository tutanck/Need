package com.aj.need.tools.utils;

import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joan on 21/10/2017.
 */

public class Jarvis<T> {

    public List<T> tr(JSONArray array, ITranslatable<T> translatable) {
        Log.d("Jarvis/tr", array.toString());
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


    public List<T> tr(QuerySnapshot querySnapshot, ITranslatable<T> translatable) {
        List<T> list = new ArrayList<T>();
        for (DocumentSnapshot documentSnapshot : querySnapshot) {
            Log.d("Jarvis/tr", documentSnapshot.getData().toString());
            T t = (T) translatable.tr(documentSnapshot);

            if (t == null) continue;
            list.add(t);
        }
        return list;
    }
}
