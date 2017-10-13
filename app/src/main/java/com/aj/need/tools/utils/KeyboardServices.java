package com.aj.need.tools.utils;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by joan on 18/09/2017.
 */

public class KeyboardServices {

    public static void dismiss(Context context, EditText editText) {
        if (context != null)//!important
            ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
