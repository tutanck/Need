package com.aj.need.domain.components.messages;

import android.util.Log;

import com.aj.need.db.colls.MESSAGES;
import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable, ITranslatable<Message> {

    private String message;
    private String to;
    private String from;
    private String conversationID;
    private Date date;
    private boolean open;

    public Message() {
    }

    public Message(String message, String from, String to, String conversationID, boolean open) {
        this.message = message;
        this.from = from;
        this.to = to;
        this.conversationID = conversationID;
        this.open = open;
    }


    public String getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }

    public Date getDate() {
        return date;
    }

    public String getTo() {
        return to;
    }

    public boolean isOpen() {
        return open;
    }

    public String getConversationID() {
        return conversationID;
    }

    @Override
    public String toString() {
        return message + " " + from + " " + to + " " + conversationID + " " + date + " " + open;
    }

    @Override
    public Message tr(DocumentSnapshot _message) {
        Log.d("loadMessages", _message.getId() + " => " + _message.getData());
        return new Message(
                _message.getString(MESSAGES.messageKey)
                , _message.getString(MESSAGES.fromKey)
                , _message.getString(MESSAGES.dateKey)
                , _message.getString(MESSAGES.conversationIDKey)
                , _message.getBoolean(MESSAGES.openKey));

    }

    @Override
    public Message tr(JSONObject json) {
        return null;
    }
}