package com.aj.need.tools.utils;

import com.aj.need.R;

/**
 * Created by joan on 07/10/2017.
 */

public class Avail {

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
            //case 2:return R.drawable.circle_red_dot; //later
            default:
                throw new RuntimeException("Avail : Unknown availability status!");
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
            //case 2:return R.drawable.circle_red_dot; //later
            default:
                throw new RuntimeException("Avail : Unknown availability status!");
        }
    }


    public static int nextStatus(int currentStatus) {
        if (currentStatus == AVAILABLE) return BUSY;
        if (currentStatus == BUSY) return AVAILABLE;
        if (currentStatus == OFFLINE) return AVAILABLE;
        else throw new RuntimeException("Avail : Unknown currentStatus!");
    }
}
