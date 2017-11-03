package com.aj.need.tools.utils;

import android.location.Location;

/**
 * Created by joan on 03/11/2017.
 */

public class _PlaceUtils {

    public static String distance(
            double latA
            , double lngA
            , double latB
            , double lngB
    ) {

        Location locationA = new Location("point A");
        locationA.setLatitude(latA);
        locationA.setLongitude(lngA);

        Location locationB = new Location("point B");
        locationB.setLatitude(latB);
        locationB.setLongitude(lngB);

        return ""+locationA.distanceTo(locationB)+"todo";//// TODO: 03/11/2017 km, m ,+900km, etc
    }

}
