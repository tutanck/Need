package com.aj.need.domain.components.profile;

import android.util.Log;

import com.aj.need.db.colls.USER_RATINGS;
import com.aj.need.tools.regina.ack.BAck;
import com.aj.need.tools.regina.ack.UIAck;
import com.aj.need.tools.utils.__;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by joan on 21/09/2017.
 */

public class UserProfile implements Serializable {

    private String _id;
    private String authID;
    private String username;
    private int reputation;
    private int availability;

    private String lastMessage;
    private String lastMessageDate;

    public UserProfile(
            String _id
            , String authID
            , String username
            , int reputation
            , int availability
    ) {
        this._id = _id;
        this.authID = authID;
        this.username = username;
        this.reputation = reputation;
        this.availability = availability;
        this.lastMessage = "";
        this.lastMessageDate = "";
    }


    public UserProfile(
            String _id
            , String authID
            , String username
            , int reputation
            , int availability
            , String lastMessage
            , String lastMessageDate
    ) {
        this(_id, authID, username, reputation, availability);
        this.lastMessage = lastMessage;
        this.lastMessageDate = lastMessageDate;
    }


    public String get_id() {
        return _id;
    }

    public String getUsername() {
        return username;
    }

    public int getReputation() {
        return reputation;
    }

    public int getAvailability() {
        return availability;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastMessageDate() {
        return lastMessageDate;
    }
}
