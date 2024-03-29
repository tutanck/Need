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

    private String conversationID, messageID, message, from, to;
    private boolean isPending = false, read = false;


    public Message() {
    }

    public Message(String message, String from, String to, String conversationID, String messageID) {
        this.message = message.trim();
        this.from = from;
        this.to = to;
        this.conversationID = conversationID;
        this.messageID = messageID;
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

    public String getMessageID() {
        return messageID;
    }

    public boolean isPending() {
        return isPending;
    }

    public boolean isRead() {
        return read;
    }


    private Message setDate(Date date) {
        super.date = date;
        return this;
    }

    public Message setPending(boolean pending) {
        isPending = pending;
        return this;
    }

    public Message setRead(boolean read) {
        this.read = read;
        return this;
    }


    @Override
    public String toString() {
        return message + " " + from + " " + to + " " + conversationID + " " + messageID + " " + read + " "+ isPending + " " + date;
    }

    @Override
    public Message tr(DocumentSnapshot _message) {
        Log.d("loadMessages", _message.getId() + " => " + _message.getData());

        Boolean readObj = _message.getBoolean(MESSAGES.readKey);

        return new Message(
                _message.getString(MESSAGES.messageKey)
                , _message.getString(MESSAGES.fromKey)
                , _message.getString(MESSAGES.toKey)
                , _message.getString(MESSAGES.conversationIDKey)
                , _message.getString(MESSAGES.messageIDKey))
                .setDate(_message.getDate(MESSAGES.dateKey))
                .setPending(_message.getMetadata().hasPendingWrites())
                .setRead((readObj == null ? false : readObj));
    }

    @Override
    public Message tr(JSONObject json) {
        return null;
    }
}