package com.aj.need.domain.components.profile;

import com.aj.need.db.colls.USERS;
import com.aj.need.tools.utils.Avail;
import com.google.firebase.firestore.DocumentSnapshot;

/**
 * Created by joan on 28/10/2017.
 */

public class Applicant extends UserProfile {

    @Override
    public UserProfile tr(DocumentSnapshot applicant) {
        return new UserProfile(
                applicant.getId()
                , applicant.getString(USERS.usernameKey)
                , 0
                , Avail.UNKNOWN
        );
    }

}
