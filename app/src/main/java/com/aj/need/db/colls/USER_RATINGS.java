package com.aj.need.db.colls;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.domain.entities.User;
import com.aj.need.domain.entities.UserRating;
import com.aj.need.tools.regina.Regina;
import com.aj.need.tools.regina.ack._Ack;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;

/**
 * Created by joan on 02/10/2017.
 */

public class USER_RATINGS implements Coll {
    private final static String coll = "USER_RATINGS";
    public final static String fromIDKey = "fromID";
    public final static String toIDKey = "toID";
    public final static String ratingKey = "rating";
    public final static String reputationKey = "reputation";

    public static Task<DocumentSnapshot> getUserRating(/*String fromID,*/ String toID/*, _Ack ack*/) {

        return IO.db.collection(USERS.coll).document(IO.auth.getUid())
                .collection(coll).document(toID).get();


        /*try {
            IO.r.find(coll
                    , __.jo().put(fromIDKey, fromID).put(toIDKey, toID)
                    , __.jo().put(ratingKey, 1).put(Coll._idKey, 0), __.jo(), ack);
        } catch (JSONException | Regina.NullRequiredParameterException e) {
            __.fatal(e);
        }*/
    }


    public static Task<Void> setUtherRating(float rating, /*String fromID,*/ String toID/*, _Ack ack*/) {

        return  IO.db.collection(USERS.coll).document(toID)
                .collection(coll).document(IO.auth.getUid()).set(new UserRating(rating));





        /*try {
            IO.r.update(coll
                    , __.jo().put(fromIDKey, fromID).put(toIDKey, toID)
                    , __.jo().put(fromIDKey, fromID).put(toIDKey, toID).put(ratingKey, rating)
                    , __.jo().put("upsert", true), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }*/
    }


    public static void computeUserRating(String userID, _Ack ack) {
        try {
            IO.socket.emit("getUserRating", __.jo().put("userID", userID), null, ack);
        } catch (JSONException e) {
            __.fatal(e);
        }
    }

}
