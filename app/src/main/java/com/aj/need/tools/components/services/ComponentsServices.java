package com.aj.need.tools.components.services;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by joan on 17/09/2017.
 */

public class ComponentsServices {

    public static void replaceView(
            View oldView
            , View newView
    ) {
        ViewGroup parent = (ViewGroup) oldView.getParent();
        parent.removeView(oldView);
        parent.addView(newView, parent.indexOfChild(oldView));
    }

    public static void setSelectable(
            Context context
            , View view,
            View.OnClickListener listenner
    ) {
        view.setClickable(true);
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        view.setBackgroundResource(outValue.resourceId);
        view.setOnClickListener(listenner);
    }

}
