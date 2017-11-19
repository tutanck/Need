package com.aj.need.tools.utils;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


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


    public static Coord toCoord(Map<String, Double> coordMap) {
        Log.d("_toCoord::Map", "" + coordMap);
        if (coordMap == null) return null;

        Double lat = coordMap.get("latitude"), lng = coordMap.get("longitude");
        if (lat == null || lng == null) return null;

        return new Coord(lat, lng);
    }


    public static Coord toCoord(JSONObject coordJSON) {
        Log.d("_toCoord::JSON", "" + coordJSON);
        if (coordJSON == null) return null;

        try {
            Double lat = null, lng = null;
            lat = coordJSON.getDouble("latitude");
            lng = coordJSON.getDouble("longitude");
            return new Coord(lat, lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }


    public Map<String, Double> toMap() {
        Map<String, Double> coordMap = new HashMap<>();
        coordMap.put("latitude", latitude);
        coordMap.put("longitude", longitude);

        return coordMap;
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