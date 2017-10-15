package com.aj.need.domain.components.messages;

import java.util.Date;

public class Message {

    private String message;
    private String to;
    private String from;
    private String conversationID;
    private Date date;
    private boolean open;

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
}