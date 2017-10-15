package com.aj.need.domain.components.profile;

import java.io.Serializable;

/**
 * Created by joan on 21/09/2017.
 */

public class UserProfile implements Serializable {

    private String _id;

    private String username;
    private int reputation;
    private int availability;

    private String conversationID;
    private String lastMessage;
    private String lastMessageDate;

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
}
