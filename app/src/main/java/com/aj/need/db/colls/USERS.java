package com.aj.need.db.colls;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.domain.entities.User;
import com.aj.need.tools.regina.Regina;
import com.aj.need.tools.regina.ack._Ack;
import com.aj.need.tools.utils.Avail;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by joan on 02/10/2017.
 */

public class USERS implements Coll {

    final static String coll = "USERS";
    public final static String collTag = "#" + coll + "/";

    public final static String authIDKey = "authID";
    public final static String typeKey = "type";
    public final static String usernameKey = "username";
    public final static String availabilityKey = "availability";


    public static Task<Void> createUser(User user) {
        return IO.db.collection(coll).document(IO.auth.getUid()).set(user);
    }

    public static DocumentReference getUserRef(String uid) {
        return IO.db.collection(coll).document(uid);
    }


    public static Task<Void> setField(/*String _id,*/ String key, Object val/*, _Ack ack*/) {
        return IO.db.collection(coll).document(IO.auth.getUid()).update(key, val);

        /*try {
            IO.r.update(coll, __.jo().put(_idKey, _id)
                    , __.jo().put("$set", __.jo().put(key, val))
                    , __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }*/

    }


    public static Task<Void> setUserAvailability(/*String _id,*/ int availability/*, _Ack ack*/) {

        return getUserRef(IO.auth.getUid()).update(availabilityKey, availability);

       /* try {
            IO.r.update(coll, __.jo().put(_idKey, _id)
                    , __.jo().put("$set", __.jo().put(availabilityKey, status))
                    , __.jo()
                    , __.jo().put(
                            "tags", __.jar().put(
                                    __.jo().put("val", collTag + _id)
                            )
                    )
                    , ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }*/
    }


    public static void getAvailability(String _id, _Ack ack) {
        try {
            IO.r.find(coll
                    , __.jo().put(_idKey, _id)
                    , __.jo().put(availabilityKey, 1)
                    , __.jo(), ack
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


    public static Task<DocumentSnapshot> getProfile(String _id/*, _Ack ack*/) {

        return IO.db.collection(coll).document(_id).get();


        /*try {
            IO.r.find(coll, __.jo().put(_idKey, _id), __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }*/
    }

    public static void getProfiles(JSONArray idList, _Ack ack) {
        try {
            IO.r.find(coll, __.jo().put(_idKey, __.jo().put("$in", idList)), __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }

}
