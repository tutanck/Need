package com.aj.need.domain.components.profile;

import com.aj.need.tools.utils.ITranslatable;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by joan on 21/09/2017.
 */

public class UserProfile implements Serializable, ITranslatable<UserProfile> {

    private String _id;

    private String username;
    private int reputation;
    private int availability;

    private String conversationID;
    private String lastMessage;
    private String lastMessageDate;


    public UserProfile() {
    }


    public UserProfile(
            String _id
            , String username
            , int reputation
            , int availability
    ) {
        this._id = _id;

        this.username = username;
        this.reputation = reputation;
        this.availability = availability;
        this.lastMessage = "";
        this.lastMessageDate = "";
    }


    public UserProfile(
            String _id
            , String username
            , int reputation
            , int availability
            , String conversationID
            , String lastMessage
            , String lastMessageDate
    ) {
        this(_id, username, reputation, availability);
        this.conversationID = conversationID;
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

    public String getConversationID() {
        return conversationID;
    }

    @Override
    public UserProfile tr(JSONObject json) {
        if (json == null) return null;

        String objectID = json.optString("objectID");
        String username = json.optString("username");
        int availability = json.optInt("availability", -1);
        int rating = json.optInt("rating", 0/*-1//todo*/);

        if (objectID != null && username != null
                && rating >= 0 && availability >= 0)
            return new UserProfile(objectID, username, rating, availability);

        return null;
    }
}
