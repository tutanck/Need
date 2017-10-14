package com.aj.need.db.colls;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.domain.components.needs.UserNeedSaveActivity;
import com.aj.need.domain.entities.User;
import com.aj.need.tools.components.fragments.FormField;
import com.aj.need.tools.regina.Regina;
import com.aj.need.tools.regina.ack._Ack;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by joan on 02/10/2017.
 */

public class NEEDS implements Coll {

    public static String coll = "NEEDS";
    public final static String ownerIDKey = "ownerID";
    public final static String activeKey = "active";
    public final static String titleKey = "title";
    public final static String descriptionKey = "description";
    public final static String searchKey = "search";


    public static Task<QuerySnapshot> loadUserNeeds(/*String ownerID, _Ack ack*/) {

        return IO.db.collection(USERS.coll).document(IO.auth.getUid()).collection(NEEDS.coll).get();

        /*try {
            IO.r.find(coll
                    , __.jo().put(ownerIDKey, ownerID).put(deletedKey, false)
                    , __.jo().put("sort", __.jo().put(activeKey, -1).put(titleKey, 1))
                    , __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }*/
    }


    public static Task<DocumentSnapshot> loadUserNeed(String _id/*, _Ack ack*/) {
        return IO.db.collection(USERS.coll).document(IO.auth.getUid()).collection(NEEDS.coll).document(_id).get();
       /* try {
            IO.r.find(coll, __.jo().put(_idKey, _id), __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }*/
    }


    public static void deactivateNeed(String _id, _Ack ack) {
        try {
            IO.r.update(coll, __.jo().put(_idKey, _id)
                    , __.jo().put("$set", __.jo().put(activeKey, false))
                    , __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


    public static Task<DocumentReference> addNeed(Map<String, Object> need) {
        return IO.db.collection(USERS.coll).document(IO.auth.getUid()).collection(coll).add(need);
    }


    public static Task setNeed(String _id, Map<String, Object> need/*String ownerID, boolean active, Map<String, FormField> formFields, _Ack ack*/) {

        return IO.db.collection(USERS.coll).document(IO.auth.getUid()).collection(NEEDS.coll).document(_id).set(need);




        /*Map<String, Object> need = new HashMap<>();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        need.put(activeKey, active);
        need.put(deletedKey, false);

        for (String key : formFields.keySet())
            if (key.equals(searchKey))
                need.put(key, formFields.get(key).getTvContent().getText());
            else
                need.put(key, formFields.get(key).getEtContent().getText());


        //it could lead to a bug if upserted docs on update mode (_id = null upsert / new doc iof update)
        if (_id == null)
            return db.collection(User.coll).document(mAuth.getUid()).collection(coll).add(need);
        else
            return db.collection(User.coll).document(mAuth.getUid()).collection(coll).document(_id).set(need);
*/

    }


    public static Task<Void> deleteNeed(String needID/*, _Ack ack*/) {
        return IO.db.collection(USERS.coll).document(IO.auth.getUid()).collection(NEEDS.coll).document(needID).update(deletedKey,true);
        /*try {
            IO.r.update(coll, __.jo().put(_idKey, needID)
                    , __.jo().put("$set", __.jo().put(deletedKey, true))
                    , __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }*/
    }


}
