package com.aj.need.tools.utils;

import com.aj.need.R;

/**
 * Created by joan on 07/10/2017.
 */

public class Avail {

    public static final int UNKNOWN = -9;
    public static final int OFFLINE = -1;
    public static final int BUSY = 0;
    public static final int AVAILABLE = 1;

    public static int getDrawable(int status) {
        switch (status) {
            case OFFLINE:
                return R.drawable.circle_red_dot;
            case BUSY:
                return R.drawable.circle_orange_dot;
            case AVAILABLE:
                return R.drawable.circle_green_dot;
            default:
                return R.drawable.circle_gray_dot;
        }
    }

    public static int getColor(int status) {
        switch (status) {
            case OFFLINE:
                return R.color.Red;
            case BUSY:
                return R.color.DarkOrange;
            case AVAILABLE:
                return R.color.Lime;
            default: return R.color.LightLightGrey;
        }
    }


    public static int nextStatus(int currentStatus) {
        switch (currentStatus) {
            case OFFLINE:
                return AVAILABLE;
            case BUSY:
                return AVAILABLE;
            case AVAILABLE:
                return BUSY;
            default:
                return UNKNOWN;
        }
    }
}
