package com.aj.need.domain.components.keywords;

/**
 * Created by joan on 21/09/2017.
 */

public class UserKeyword {

    public final static String coll = "USER_KEYWORDS";

    public final static String activeKey = "active";
    public final static String keywordKey = "keyword";
    public final static String deletedKey = "deleted";

    private String keyword;
    private boolean active;
    private boolean deleted;

    public UserKeyword(String keyword, boolean active) {
        this.keyword = keyword;
        this.active = active;
        this.deleted=false;
    }

    public UserKeyword(String keyword, boolean active, boolean deleted) {
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
