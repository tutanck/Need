package com.aj.need.tools.utils;

import android.text.format.DateUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * App class with static util methods.
 */
public class _DateUtils {

    // This class should not be initialized
    private _DateUtils() {
    }

    //https://developer.android.com/reference/android/text/format/DateUtils.html#formatSameDayTime(long,%20long,%20int,%20int)
    //https://www.javatips.net/api/UnivrApp-master/src/com/cellasoft/univrapp/utils/DateUtils.java
    public static CharSequence since(Date date) {
        return date == null ? null : DateUtils.formatSameDayTime(date.getTime()
                , new Timestamp(System.currentTimeMillis()).getTime()
                , DateFormat.MEDIUM, DateFormat.SHORT);
    }






    /*FROM GOOGLE @codebase: https://stackoverflow.com/questions/13018550/time-since-ago-library-for-android-java */

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String ago(Date date) {
        if (date == null) return null;

        long time = date.getTime();
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis(); //subject to improvement if possible @see https://stackoverflow.com/questions/5175728/how-to-get-the-current-date-time-in-java
        if (time > now || time <= 0) {
            return null;
        }

        String ago = "il y a ";

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) return "à l'instant";

        else if (diff < 2 * MINUTE_MILLIS) return ago + "1 minute";

        else if (diff < 50 * MINUTE_MILLIS) return ago + (diff / MINUTE_MILLIS) + " minutes";

        else if (diff < 90 * MINUTE_MILLIS) return ago + "1 heure";

        else if (diff < 24 * HOUR_MILLIS) return ago + (diff / HOUR_MILLIS) + " heures";

        else if (diff < 48 * HOUR_MILLIS) return "hier";

        else return ago + (diff / DAY_MILLIS) + " jours";

    }






    /*FROM SEND-BIRD @codebase:  https://github.com/smilefam/SendBird-Android/blob/master/app/src/main/java/com/sendbird/android/sample/utils/DateUtils.java */

    /**
     * Gets timestamp in millis and converts it to HH:mm (e.g. 16:44).
     */
    public static String formatTime(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * If the given time is of a different date, display the date.
     * If it is of the same date, display the time.
     *
     * @param timeInMillis The time to convert, in milliseconds.
     * @return The time or date.
     */
    public static String formatDateTime(long timeInMillis) {
        if (isToday(timeInMillis)) {
            return formatTime(timeInMillis);
        } else {
            return formatDate(timeInMillis);
        }
    }

    /**
     * Formats timestamp to 'date month' format (e.g. 'February 3').
     */
    public static String formatDate(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        return dateFormat.format(timeInMillis);
    }

    /**
     * Returns whether the given date is today, based on the user's current locale.
     */
    public static boolean isToday(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String date = dateFormat.format(timeInMillis);
        return date.equals(dateFormat.format(System.currentTimeMillis()));
    }

    /**
     * Checks if two dates are of the same day.
     *
     * @param millisFirst  The time in milliseconds of the first date.
     * @param millisSecond The time in milliseconds of the second date.
     * @return Whether {@param millisFirst} and {@param millisSecond} are off the same day.
     */
    public static boolean hasSameDate(long millisFirst, long millisSecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return dateFormat.format(millisFirst).equals(dateFormat.format(millisSecond));
    }
}
