package com.aj.need.domain.components.keywords;

/**
 * Created by joan on 21/09/2017.
 */

public class UserKeyword {

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
}
