package com.aj.need.tools.utils;

import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

/**
 * Created by joan on 21/10/2017.
 */

public interface ITranslatable<T> {

    public T tr(JSONObject json);

    public T tr(DocumentSnapshot documentSnapshot);
}
