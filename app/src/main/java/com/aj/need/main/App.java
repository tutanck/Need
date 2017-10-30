package com.aj.need.main;

import android.app.Application;
import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class App extends Application {

    private Map<String, Uri> imageUriMap = new HashMap<>();

    public void setImageUri(String id, Uri uri) {
        imageUriMap.put(id,uri);
    }

    public Uri getImageUri(String id) {
        return imageUriMap.get(id);
    }
}