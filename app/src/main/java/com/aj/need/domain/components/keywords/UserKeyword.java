package com.aj.need.domain.components.keywords;

import com.aj.need.db.colls.USER_KEYWORDS;
import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by joan on 21/09/2017.
 */

public class UserKeyword implements Serializable, ITranslatable<UserKeyword> {

    private String keyword;
    private boolean active;
    private boolean deleted;


    public UserKeyword() {
    }

    UserKeyword(String keyword, boolean active, boolean deleted) {
        this.keyword = keyword;
        this.active = active;
        this.deleted = deleted;
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDeleted() {
        return deleted;
    }


    @Override
    public UserKeyword tr(DocumentSnapshot keyword) {
        return new UserKeyword(
                keyword.getId()
                , keyword.getBoolean(USER_KEYWORDS.activeKey)
                , keyword.getBoolean(USER_KEYWORDS.deletedKey));
    }

    @Override
    public UserKeyword tr(JSONObject json) {
        return null;
    }
}
