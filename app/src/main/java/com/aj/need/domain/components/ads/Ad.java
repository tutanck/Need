package com.aj.need.domain.components.ads;

import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.Serializable;

public class Ad implements Serializable, ITranslatable<Ad> {

    private String search;
    private String ownerID;
    private String updatedAt;

    private Ad(String search, String ownerID, String updatedAt) {
        this.search = search;
        this.ownerID = ownerID;
        this.updatedAt = updatedAt;
    }


    public String getSearch() {
        return search;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public String toString() {
        return search + " " + ownerID + " " + updatedAt;
    }

    @Override
    public Ad tr(JSONObject json) {
        return null;
    }

    @Override
    public Ad tr(DocumentSnapshot documentSnapshot) {
        return null;
    }
}