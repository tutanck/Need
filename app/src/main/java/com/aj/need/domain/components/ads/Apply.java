package com.aj.need.domain.components.ads;

import com.aj.need.domain.entities.Entity;
import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by joan on 07/11/2017.
 */

public class Apply extends Entity implements Serializable, ITranslatable<Apply> {

    private String username;
    private String needTitle;
    private boolean active;


    Apply(String username, String needTitle) {
        this.username = username;
        this.needTitle = needTitle;
        this.active = true;
    }


    public String getUsername() {
        return username;
    }

    public String getNeedTitle() {
        return needTitle;
    }

    public boolean isActive() {
        return active;
    }


    @Override
    public Apply tr(JSONObject json) {
        return null;
    }

    @Override
    public Apply tr(DocumentSnapshot documentSnapshot) {
        return null;
    }


}
