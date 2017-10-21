package com.aj.need.tools.utils;

import org.json.JSONObject;

/**
 * Created by joan on 21/10/2017.
 */

public interface ITranslatable<T> {

    public T tr(JSONObject json);
}
