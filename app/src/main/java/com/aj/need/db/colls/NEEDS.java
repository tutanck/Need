package com.aj.need.db.colls;

import com.aj.need.db.IO;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.tools.components.fragments.FormField;
import com.aj.need.tools.regina.Regina;
import com.aj.need.tools.regina.ack._Ack;
import com.aj.need.tools.utils.__;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by joan on 02/10/2017.
 */

public class NEEDS implements Coll {

    private static String coll = "NEEDS";
    public final static String ownerIDKey = "ownerID";
    public final static String activeKey = "active";
    public final static String titleKey = "title";
    public final static String descriptionKey = "description";
    public final static String searchKey = "search";


    public static void loadNeeds(String ownerID, _Ack ack) {
        try {
            IO.r.find(coll
                    , __.jo().put(ownerIDKey, ownerID).put(deletedKey, false)
                    , __.jo().put("sort", __.jo().put(activeKey, -1).put(titleKey, 1))
                    , __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


    public static void loadNeed(String _id, _Ack ack) {
        try {
            IO.r.find(coll, __.jo().put(_idKey, _id), __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
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


    public static void saveNeed(String _id, String ownerID, boolean active, Map<String, FormField> formFields, _Ack ack) {
        try {
            JSONObject need = __.jo().put(activeKey, active).put(ownerIDKey, ownerID);

            for (String key : formFields.keySet())
                if (key.equals(searchKey))
                    need.put(key, formFields.get(key).getTvContent().getText());
                else
                    need.put(key, formFields.get(key).getEtContent().getText());

            //it could be lead to a bug if upserted docs on update mode (_id = null upsert / new doc iof update)
            if (_id == null)
                IO.r.insert(coll, need.put(deletedKey, false), __.jo(), __.jo(), ack);
            else
                IO.r.update(coll, __.jo().put(_idKey, _id), __.jo().put("$set", need), __.jo(), __.jo(), ack);

        } catch (JSONException | Regina.NullRequiredParameterException e) {
            __.fatal(e);
        }
    }


    public static void deleteNeed(String needID,_Ack ack) {
        try {
            IO.r.update(coll, __.jo().put(_idKey, needID)
                    , __.jo().put("$set", __.jo().put(deletedKey, true))
                    , __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


}
