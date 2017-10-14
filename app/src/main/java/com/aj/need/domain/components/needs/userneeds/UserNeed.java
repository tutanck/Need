package com.aj.need.domain.components.needs.userneeds;

import java.io.Serializable;

/**
 * Created by joan on 21/09/2017.
 */

public class UserNeed implements Serializable {

    private String _id;
    private String title;
    private String searchText;
    private boolean active;
    private final boolean deleted = false; //make no sense to instantiate a deleted need

    public UserNeed() {
    }

    UserNeed(
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
}
