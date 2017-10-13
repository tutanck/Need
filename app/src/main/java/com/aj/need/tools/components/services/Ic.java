package com.aj.need.tools.components.services;

import com.aj.need.R;

/**
 * Created by joan on 02/10/2017.
 */

public class Ic {

    public static int icon(String key) {
        switch (key) {
            case "username":
                return R.drawable.ic_person_24dp;
            case "resume":
                return R.drawable.ic_work_24dp;
            case "tariff":
                return R.drawable.ic_euro_symbol_24dp;

            case "search":
                return R.drawable.ic_search_gold_24dp;
            case "title":
                return R.drawable.ic_bookmark_24dp;
            case "description":
                return R.drawable.ic_description_24dp;
            case "reward":
                return R.drawable.ic_euro_symbol_24dp;
            case "when":
                return R.drawable.ic_access_time_24dp;
            case "where":
                return R.drawable.ic_place_24dp;

            default: throw new RuntimeException("Unknown icon key");
        }
    }
}
