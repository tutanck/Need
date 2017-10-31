package com.aj.need.domain.components.needs.userneeds;

import com.aj.need.db.colls.USER_NEEDS;
import com.aj.need.domain.entities.Entity;
import com.aj.need.tools.utils.Coord;
import com.aj.need.tools.utils.ITranslatable;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by joan on 21/09/2017.
 */

public class UserNeed extends Entity implements Serializable, ITranslatable<UserNeed> {

    private String _id;
    private String ownerID;

    private String search;
    private String title;
    private String description;
    private String reward;
    private String where; //!important : should be the string not a position (no conversion to apply )
    private String when; //!important : should be the string not a date (no conversion to apply )

    private Coord metaWhereCoord;
    private Long metaWhenTime;//!important : do not use for now : unreliable inter-devices

    private boolean active;
    private final boolean deleted = false; //make no sense to instantiate (nor to retrieve) a deleted need

    public UserNeed() {
    }

    public UserNeed(
            String _id
            , String ownerID

            , String search
            , String title
            , String description
            , String reward
            , String where
            , String when

            , Coord metaWhereCoord
            , Long metaWhenTime

            , boolean active
    ) {
        this._id = _id;
        this.ownerID = ownerID;

        this.search = search;
        this.title = title;
        this.description = description;
        this.reward = reward;
        this.where = where;
        this.when = when;

        this.metaWhereCoord = metaWhereCoord;
        this.metaWhenTime = metaWhenTime;

        this.active = active;
    }


    @Override
    public UserNeed tr(DocumentSnapshot need) {
        return new UserNeed(need.getId()
                , need.getString(USER_NEEDS.ownerIDKey)

                , need.getString(USER_NEEDS.searchKey)
                , need.getString(USER_NEEDS.titleKey)
                , need.getString(USER_NEEDS.descriptionKey)
                , need.getString(USER_NEEDS.rewardKey)
                , need.getString(USER_NEEDS.whereKey)
                , need.getString(USER_NEEDS.whenKey)

                , toCoord(need.get(USER_NEEDS.metaWhereCoordKey))
                , need.getLong(USER_NEEDS.metaWhenTimeKey)

                , need.getBoolean(USER_NEEDS.activeKey))
                .setDate(need.getDate(USER_NEEDS.dateKey));
    }


    private UserNeed setDate(Date date){
        super.date = date;
        return this;
    }

    private Coord toCoord(Object o) {
        return o == null ? null : new Coord(
                ((HashMap<Double, Double>) o).get("latitude")
                , ((HashMap<Double, Double>) o).get("longitude")
        );

    }


    public String get_id() {
        return _id;
    }

    public String getOwnerID() {
        return ownerID;
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

    public String getWhen() {
        return when;
    }


    public Coord getMetaWhereCoord() {
        return metaWhereCoord;
    }

    public Long getMetaWhenTime() {
        return metaWhenTime;
    }


    public boolean isActive() {
        return active;
    }

    public boolean isDeleted() {
        return deleted;
    }


    @Override
    public UserNeed tr(JSONObject json) {
        return null;
    }

}
