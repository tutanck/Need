package com.aj.need.db.colls;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.tools.regina.Regina;
import com.aj.need.tools.regina.ack._Ack;
import com.aj.need.tools.utils.__;

import org.json.JSONException;

/**
 * Created by joan on 02/10/2017.
 */

public class USER_KEYWORDS implements Coll {
    private static String coll = "USER_KEYWORDS";

    public final static String userIDKey = "userID";
    public final static String activeKey = "active";
    public final static String keywordKey = "keyword";


    public static void loadUtherKeywords(String userID, _Ack ack) {
        try {
            IO.r.find(coll
                    , __.jo().put(userIDKey, userID).put(activeKey, true).put(deletedKey, false)
                    , __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


    public static void loadUserKeywords(String userID, _Ack ack) {
        try {
            IO.r.find(
                    coll
                    , __.jo().put(userIDKey, userID).put(deletedKey, false)
                    , __.jo().put("sort", __.jo().put(activeKey, -1).put(keywordKey, 1))
                    , __.jo(), ack
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


    public static void saveUserKeyword(String keyword, String userID, boolean active, boolean deleted, _Ack ack) {
        try {
            IO.r.update(coll
                    , __.jo().put(userIDKey, userID).put(keywordKey, keyword)
                    , __.jo().put(userIDKey, userID).put(keywordKey, keyword).put(activeKey, active).put(deletedKey, deleted)
                    , __.jo().put("upsert", true), __.jo(), ack
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


}
