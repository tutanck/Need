package com.aj.need.tools.utils;

import android.location.Location;

/**
 * Created by joan on 03/11/2017.
 */

public class _PlaceUtils {

    private static final int kmUnit = 1000;
    private static final String kmStr = " km";

    public static String distance(Location locationA, Location locationB) {
        String distStr = "";

        if (locationA != null && locationB != null) {
            Integer dist = Math.round(locationA.distanceTo(locationB) / kmUnit);

            if (dist < 1) distStr = "< 1";
            else if (dist > 900) distStr = "> 900";
            else distStr = dist.toString();

            distStr = distStr + kmStr;
        }

        return distStr;
    }


    public static String distance(Location locationA, double latB, double lngB) {
        Location locationB = new Location("point B");
        locationB.setLatitude(latB);
        locationB.setLongitude(lngB);

        return distance(locationA, locationB);
    }

}
