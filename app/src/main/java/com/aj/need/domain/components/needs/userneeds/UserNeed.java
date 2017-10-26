package com.aj.need.domain.components.needs.userneeds;

import com.aj.need.db.colls.USER_NEEDS;
import com.aj.need.domain.entities.Entity;
import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by joan on 21/09/2017.
 */

public class UserNeed extends Entity implements Serializable, ITranslatable<UserNeed> {

    private String _id;
    private String title;
    private String searchText;
    private boolean active;
    private final boolean deleted = false; //make no sense to instantiate a deleted need

    public UserNeed() {
    }

    private UserNeed(
            String _id
            , String title
            , String search
            , boolean active
    ) {
        this._id = _id;
        this.title = title;
        this.searchText = search;
        this.active = active;
    }


    public String get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isActive() {
        return active;
    }

    public String getSearchText() {
        return searchText;
    }

    public boolean isDeleted() {
        return deleted;
    }


    @Override
    public UserNeed tr(DocumentSnapshot need) {
        return new UserNeed(need.getId()
                , need.getString(USER_NEEDS.titleKey)
                , need.getString(USER_NEEDS.searchKey)
                , need.getBoolean(USER_NEEDS.activeKey));
    }

    @Override
    public UserNeed tr(JSONObject json) {
        return null;
    }

}
