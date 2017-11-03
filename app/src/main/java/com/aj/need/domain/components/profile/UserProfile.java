package com.aj.need.domain.components.profile;

import com.aj.need.domain.entities.Entity;
import com.aj.need.tools.utils.Avail;
import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by joan on 21/09/2017.
 */

public class UserProfile extends Entity implements Serializable, ITranslatable<UserProfile> {

    private String _id;

    private String username;
    private int reputation;
    private int availability;

    private String conversationID;
    private String lastMessage;
    private Date lastMessageDate;


    public UserProfile() {
    }


    private UserProfile(
            String _id
            , String username
            , int reputation
            , int availability
    ) {
        this._id = _id;

        this.username = username;
        this.reputation = reputation;
        this.availability = availability;
    }


    protected /*!important : only for Contact class usage*/
    UserProfile(
            String _id
            , String conversationID
            , String lastMessage
            , Date lastMessageDate
    ) {
        this(_id, null, 0, Avail.UNKNOWN, conversationID, lastMessage, lastMessageDate);
    }


    public UserProfile(
            String _id
            , String username
            , int reputation
            , int availability
            , String conversationID
            , String lastMessage
            , Date lastMessageDate
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

    public void setUsername(String username) {
        this.username = username;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(Long reputation) {

        this.reputation = reputation != null ? reputation.intValue() : 0;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public Date getLastMessageDate() {
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
        int availability = json.optInt("availability", Avail.UNKNOWN);
        int rating = json.optInt("rating", 0);

        if (objectID != null && username != null && availability >= 0)
            return new UserProfile(objectID, username, rating, availability);

        return null;
    }

    @Override
    public UserProfile tr(DocumentSnapshot documentSnapshot) {
        return null;
    }
}
