package com.aj.need.domain.components.profile;

import com.aj.need.db.colls.MESSAGES;
import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.tools.utils.Avail;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

/**
 * Created by joan on 28/10/2017.
 */

public class Contact extends UserProfile {


    private String conversationID, messageID, message, from, to;
    private boolean read;


    public Contact() {
    }


    private Contact(
            String _id
            , String conversationID
            , String messageID
            , String message
            , String from
            , String to
            , Boolean readObj
            , Date date
    ) {
        super(_id, null, 0, Avail.UNKNOWN);
        this.conversationID = conversationID;
        this.messageID = messageID;
        this.message = message;
        this.from = from;
        this.to = to;
        this.read = (readObj == null ? false : readObj);
        super.date = date;
    }


    public String getConversationID() {
        return conversationID;
    }

    public String getMessageID() {
        return messageID;
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

    public boolean isRead() {
        return read;
    }


    @Override
    public UserProfile tr(DocumentSnapshot contactDoc) {
        Boolean readObj = contactDoc.getBoolean(MESSAGES.readKey);
        return new Contact(
                contactDoc.getId()
                , contactDoc.getString(MESSAGES.conversationIDKey)
                , contactDoc.getString(MESSAGES.messageIDKey)
                , contactDoc.getString(MESSAGES.messageKey)
                , contactDoc.getString(MESSAGES.fromKey)
                , contactDoc.getString(MESSAGES.toKey)
                , contactDoc.getBoolean(MESSAGES.readKey)
                , contactDoc.getDate(MESSAGES.dateKey)
        );
    }

}
