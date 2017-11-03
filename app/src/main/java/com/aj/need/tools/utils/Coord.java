package com.aj.need.tools.utils;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;


/**
 * Created by joan on 29/10/2017.
 */

public class Coord implements Serializable {
    private final double latitude;
    private final double longitude;

    public Coord(double lat, double lng) {
        this.latitude = lat;
        this.longitude = lng;
    }


    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public String toString() {
        double lat = this.latitude;
        double lng = this.longitude;
        return (new StringBuilder()).append("GeoPt { latitude=").append(lat).append(", longitude=").append(lng).append(" }").toString();
    }

    public LatLng toLatLng() {
        return new LatLng(this.getLatitude(), this.getLongitude());
    }

}