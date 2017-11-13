package com.aj.need.tools.components.services;

import com.aj.need.R;
import com.aj.need.db.colls.USERS;
import com.aj.need.db.colls.USER_NEEDS;

/**
 * Created by joan on 02/10/2017.
 */

public class Ic {

    public static int icon(String key) {
        switch (key) {
            case USERS.usernameKey:
                return R.drawable.ic_person_24dp;
            case USERS.resumeKey:
                return R.drawable.ic_work_24dp;
            case USERS.tariffKey:
                return R.drawable.ic_euro_symbol_24dp;
            case USERS.localityKey:
                return R.drawable.ic_place_24dp;

            case USER_NEEDS.searchKey:
                return R.drawable.ic_search_gold_24dp;
            case USER_NEEDS.titleKey:
                return R.drawable.ic_bookmark_24dp;
            case USER_NEEDS.descriptionKey:
                return R.drawable.ic_description_24dp;
            case USER_NEEDS.rewardKey:
                return R.drawable.ic_euro_symbol_24dp;
            case USER_NEEDS.whenKey:
                return R.drawable.ic_access_time_24dp;
            case USER_NEEDS.whereKey:
                return R.drawable.ic_place_24dp;

            default:
                throw new RuntimeException("Unknown icon key : "+key);
        }
    }
}
