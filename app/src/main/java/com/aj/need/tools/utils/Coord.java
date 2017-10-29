package com.aj.need.tools.utils;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by joan on 29/10/2017.
 */

public class Coord implements Serializable {
    private final double latitude;
    private final double longitude;

    public Coord(double var1, double var2) {
        this.latitude = var1;
        this.longitude = var2;
    }

    public Coord(LatLng latLng) {
        this(latLng.latitude, latLng.longitude);
    }


    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String toString() {
        double var1 = this.latitude;
        double var3 = this.longitude;
        return (new StringBuilder()).append("GeoPt { latitude=").append(var1).append(", longitude=").append(var3).append(" }").toString();
    }

}