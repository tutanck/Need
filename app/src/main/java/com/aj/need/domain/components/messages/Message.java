package com.aj.need.domain.components.messages;

import android.util.Log;

import com.aj.need.db.colls.MESSAGES;
import com.aj.need.domain.entities.Entity;
import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class Message extends Entity implements Serializable, ITranslatable<Message> {

    private String message;
    private String from;
    private String to;
    private String conversationID;
    private boolean isPending = false;


    public Message() {
    }

    public Message(String message, String from, String to, String conversationID) {
        this.message = message.trim();
        this.from = from;
        this.to = to;
        this.conversationID = conversationID;
    }


    public String getMessage() {
        return message;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getConversationID() {
        return conversationID;
    }

    public boolean isPending() {
        return isPending;
    }

    private Message setDate(Date date) {
        super.date = date;
        return this;
    }

    public Message setPending(boolean pending) {
        isPending = pending;
        return this;
    }

    @Override
    public String toString() {
        return message + " " + from + " " + to + " " + conversationID + " " + date;
    }

    @Override
    public Message tr(DocumentSnapshot _message) {
        Log.d("loadMessages", _message.getId() + " => " + _message.getData());
        return new Message(
                _message.getString(MESSAGES.messageKey)
                , _message.getString(MESSAGES.fromKey)
                , _message.getString(MESSAGES.toKey)
                , _message.getString(MESSAGES.conversationIDKey))
                .setDate(_message.getDate(MESSAGES.dateKey))
                .setPending(_message.getMetadata().hasPendingWrites());

    }

    @Override
    public Message tr(JSONObject json) {
        return null;
    }
}