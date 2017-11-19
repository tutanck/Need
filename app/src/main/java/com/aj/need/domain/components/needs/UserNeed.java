package com.aj.need.domain.components.needs;

import com.aj.need.db.colls.USER_NEEDS;
import com.aj.need.domain.entities.Entity;
import com.aj.need.tools.utils.Coord;
import com.aj.need.tools.utils.ITranslatable;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by joan on 21/09/2017.
 */

public class UserNeed extends Entity implements Serializable, ITranslatable<UserNeed> {

    private String _id;
    private String ownerID;
    private String ownerName;

    private String search;
    private String title;
    private String description;
    private String reward;
    private String where; //!important : should be the string not a position (no conversion to apply )

    private boolean metaIsWhereVisible;
    private Coord metaWhereCoord;

    private boolean active;
    private final boolean deleted = false; //make no sense to instantiate (nor to retrieve) a deleted need

    public UserNeed() {
    }

    public UserNeed(
            String _id
            , String ownerID
            , String ownerName

            , String search
            , String title
            , String description
            , String reward
            , String where
            , boolean metaIsWhereVisible
            , Coord metaWhereCoord

            , boolean active
    ) {
        this._id = _id;
        this.ownerID = ownerID;
        this.ownerName = ownerName;

        this.search = search;
        this.title = title;
        this.description = description;
        this.reward = reward;
        this.where = where;

        this.metaIsWhereVisible = metaIsWhereVisible;
        this.metaWhereCoord = metaWhereCoord;

        this.active = active;
    }


    @Override
    public UserNeed tr(DocumentSnapshot need) {
        return new UserNeed(need.getId()
                , need.getString(USER_NEEDS.ownerIDKey)
                , need.getString(USER_NEEDS.ownerNameKey)

                , need.getString(USER_NEEDS.searchKey)
                , need.getString(USER_NEEDS.titleKey)
                , need.getString(USER_NEEDS.descriptionKey)
                , need.getString(USER_NEEDS.rewardKey)
                , need.getString(USER_NEEDS.whereKey)

                , need.getBoolean(USER_NEEDS.metaIsWhereVisibleKey)
                , Coord.toCoord((Map<String, Double>) need.get(USER_NEEDS.metaWhereCoordKey))

                , need.getBoolean(USER_NEEDS.activeKey))
                .setDate(need.getDate(USER_NEEDS.dateKey));
    }


    @Override
    public UserNeed tr(JSONObject json) {
        if (json == null) return null;

        String objectID = json.optString("objectID");

        String ownerID = json.optString(USER_NEEDS.ownerIDKey);
        String ownerName = json.optString(USER_NEEDS.ownerNameKey);

        String search = json.optString(USER_NEEDS.searchKey);
        String title = json.optString(USER_NEEDS.titleKey);
        String description = json.optString(USER_NEEDS.descriptionKey);
        String reward = json.optString(USER_NEEDS.rewardKey);
        String where = json.optString(USER_NEEDS.whereKey);

        boolean metaIsWhereVisible = json.optBoolean(USER_NEEDS.metaIsWhereVisibleKey);
        JSONObject metaWhereCoord = json.optJSONObject(USER_NEEDS.metaWhereCoordKey);

        boolean active = json.optBoolean(USER_NEEDS.activeKey);
        String date = json.optString(USER_NEEDS.dateKey);

        if (!json.optBoolean(USER_NEEDS.deletedKey))
            return new UserNeed(objectID
                    , ownerID, ownerName
                    , search, title, description, reward, where
                    , metaIsWhereVisible
                    , Coord.toCoord(metaWhereCoord)
                    , active).setDate(null);//TODO: 19/11/2017

        return null;
    }


    private UserNeed setDate(Date date) {
        super.date = date;
        return this;
    }


    public String get_id() {
        return _id;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getSearch() {
        return search;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getReward() {
        return reward;
    }

    public String getWhere() {
        return where;
    }

    public boolean isMetaIsWhereVisible() {
        return metaIsWhereVisible;
    }

    public Coord getMetaWhereCoord() {
        return metaWhereCoord;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
