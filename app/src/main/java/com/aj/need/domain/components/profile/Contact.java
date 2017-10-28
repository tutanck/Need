package com.aj.need.domain.components.profile;

import com.aj.need.db.colls.MESSAGES;
import com.aj.need.domain.components.profile.UserProfile;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by joan on 28/10/2017.
 */

public class Contact extends UserProfile {

    @Override
    public UserProfile tr(DocumentSnapshot contactDoc) {
        return new UserProfile(
                contactDoc.getId()
                , contactDoc.getString(MESSAGES.conversationIDKey)
                , contactDoc.getString(MESSAGES.messageKey)
                , contactDoc.getDate(MESSAGES.dateKey)
        );
    }


}
