package com.aj.need.tools.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aj.need.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by joan on 19/09/2017.
 */

public class __ {

    public static final void apologize(Activity ctx, boolean flee) {
        Toast.makeText(ctx, ctx.getString(R.string.an_error_occured), Toast.LENGTH_LONG).show();
        if (flee) ctx.finish();
    }


    public static final void showLongToast(Context ctx, String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }

    public static final void showShortToast(Context ctx, String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_SHORT).show();
    }


    public static Snackbar showShortSnack(View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_SHORT);
        snackbar.show();
        return snackbar;
    }

    public static Snackbar showLongSnack(View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        snackbar.show();
        return snackbar;
    }


    public static boolean match(String regex, String text) {
        return Pattern.compile(regex).matcher(text).matches();
    }

    public static boolean found(String regex, String text) {
        return Pattern.compile(regex).matcher(text).find();
    }


    public static final Map<String, Object> obj() {
        Map<String, Object> obj = new HashMap<>();
        return obj;
    }


    public static String ordered_concat(String str1, String str2) {
        String separator = "_";
        String s1 = str1.trim();
        String s2 = str2.trim();
        return s1.compareTo(s2) < 0 ? s1 + separator + s2 : s2 + separator + s1;
    }


    public static final JSONObject jo() {
        return new JSONObject();
    }

    public static final JSONArray jar() {
        return new JSONArray();
    }


    public static void fatal(Throwable throwable) {
        throw new RuntimeException(throwable);
    }

    public static void fatal(String message) {
        throw new RuntimeException(message);
    }


    public static void chill(String who) {
        Log.i("@" + who, "is chillin");
    }

    public static Object soften(Object o) {
        return o == null ? null : o;
    }
}
