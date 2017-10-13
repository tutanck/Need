package com.aj.need.domain.components.keywords;

/**
 * Created by joan on 21/09/2017.
 */

class UserKeyword {

    private String keyword;
    private boolean active;

    UserKeyword(
            String keyword
            , boolean active
    ) {
        this.keyword = keyword;
        this.active = active;
    }

    String getKeyword() {
        return keyword;
    }

    boolean isActive() {
        return active;
    }
}
