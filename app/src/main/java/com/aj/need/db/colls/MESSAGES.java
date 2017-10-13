package com.aj.need.db.colls;

import android.util.Log;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.tools.regina.Regina;

import com.aj.need.tools.regina.ack._Ack;
import com.aj.need.tools.utils.__;

import org.json.JSONException;

import java.util.Date;

/**
 * Created by joan on 02/10/2017.
 */

public class MESSAGES implements Coll {
    private final static String coll = "MESSAGES";
    public final static String collTag = "#" + coll + "/";

    public final static String senderIDKey = "senderID";
    public final static String toIDKey = "toID";
    public final static String messageKey = "message";


    public static void loadMessages(String aID, String bID, _Ack ack) {
        try {
            IO.r.find(
                    coll
                    , __.jo().put(
                            "$or"
                            , __.jar()
                                    .put(__.jo().put(senderIDKey, aID).put(toIDKey, bID))
                                    .put(__.jo().put(toIDKey, aID).put(senderIDKey, bID))
                    )
                    , __.jo().put("sort", __.jo().put(dateKey, 1))
                    , __.jo(), ack
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


    public static void sendMessage(String senderID, String toID, String text, _Ack ack) {
        try {
            IO.socket.emit("sendMessage"
                    , __.jo()
                            .put(senderIDKey, senderID)
                            .put(toIDKey, toID)
                            .put(messageKey, text)
                    , __.jo().put(
                            "tags", __.jar().put(__.jo().put("val", collTag + senderID+"/"+toID))
                    )
                    , ack);
        } catch (JSONException e) {
            __.fatal(e);
        }
    }


    public static void computeUserContacts(String userID, _Ack ack) {
        try {
            IO.socket.emit("getUserContacts", __.jo().put("userID", userID), null, ack);
        } catch (JSONException e) {
            __.fatal(e);
        }
    }

}
