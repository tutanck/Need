package com.aj.need.domain.components.profile;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.colls.USERS;
import com.aj.need.domain.components.messages.MessagesActivity;
import com.aj.need.tools.utils.Avail;
import com.aj.need.tools.utils._DateUtils;
import com.aj.need.tools.utils._Storage;
import com.aj.need.tools.utils.__;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

/**
 * Created by joan on 21/09/17.
 */
public class UserProfilesRecyclerAdapter extends RecyclerView.Adapter<UserProfilesRecyclerAdapter.ViewHolder> {

    private ArrayList<UserProfile> mProfiles;
    private Context mContext;
    private int mOnClickListenerType;

    public UserProfilesRecyclerAdapter(
            Context context,
            ArrayList<UserProfile> profiles,
            int onClickListenerType
    ) {
        mContext = context;
        mProfiles = profiles;
        mOnClickListenerType = onClickListenerType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_user_profile, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(mProfiles.get(position), mContext, mOnClickListenerType);
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView userProfileIV;
        private TextView usernameTV;
        private FloatingActionButton userStatusFAB;
        private RatingBar userReputationRBar;
        private TextView userDistanceTV;
        private TextView messageTV;
        private TextView messageDateTV;


        private UserProfile mUserProfile;
        private Context mContext;
        private int mOnClickListenerType;


        public ViewHolder(View v) {
            super(v);

            userProfileIV = v.findViewById(R.id.userProfileIV);
            usernameTV = v.findViewById(R.id.usernameTV);
            userStatusFAB = v.findViewById(R.id.userStatusFAB);
            userReputationRBar = v.findViewById(R.id.userReputationRBar);
            userDistanceTV = v.findViewById(R.id.userDistanceTV);
            messageTV = v.findViewById(R.id.messageTV);
            messageDateTV = v.findViewById(R.id.messageDateTV);
            v.setOnClickListener(this);
        }


        public void bindItem(UserProfile userProfile, Context context, int onClickListenerType) {
            this.mUserProfile = userProfile;
            this.mContext = context;
            this.mOnClickListenerType = onClickListenerType;

            setLastMessage();
            setProfile();

            userProfileIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtherProfileActivity.start(mContext, mUserProfile.get_id());
                }
            });


            if (mUserProfile.getUsername() == null || mUserProfile.getAvailability() == Avail.UNKNOWN) {
                Log.d("bindItem/", "UserProfilesRecyclerAdapter::getUser");
                USERS.getUserRef(mUserProfile.get_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot userDoc) {
                        Log.d("bindItem/", "UserProfilesRecyclerAdapter::onSuccess: data=" + userDoc.getData());
                        if (userDoc.exists()) {
                            mUserProfile.setAvailability(userDoc.getLong(USERS.availabilityKey).intValue());
                            mUserProfile.setReputation(userDoc.getLong(USERS.avgRatingKey).intValue());
                            mUserProfile.setUsername(userDoc.getString(USERS.usernameKey));
                            setProfile();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("bindItem/", "UserProfilesRecyclerAdapter::onFailure", e);
                    }
                });
            }

            _Storage.loadRef(_Storage.getRef(userProfile.get_id()))
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //todo @see https://github.com/bumptech/glide/issues/803 and try @AllanWang sol if pb : init Glide with the mContext asap (in the UserProfilesRecyclerAdapter's constructor of if possible even sooner)
                            //I faced the same issue .. i fixed it like that :Glide.with(mContext.getApplicationContext()) //activity.getApplicationContext()
                            //from @AllanWang : @tutanck I think that removes the whole life cycle handling. Best I initialize it with the activity context on creation or validate beforehand
                            Glide.with(mContext.getApplicationContext()/*!important*/) //fix of : Glide's Fatal Exception: java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity
                                    .load(uri)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(userProfileIV);
                        }
                    });
        }


        private void setProfile() {
            usernameTV.setText(mUserProfile.getUsername());
            userStatusFAB.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor
                            (mContext, Avail.getColor(mUserProfile.getAvailability()))));
            userReputationRBar.setRating(mUserProfile.getReputation());
        }


        private void setLastMessage() {
            messageTV.setText(mUserProfile.getLastMessage());
            messageDateTV.setText(_DateUtils.since(mUserProfile.getLastMessageDate()));
        }


        @Override
        public void onClick(View view) {
            switch (mOnClickListenerType) {
                case 1:
                    MessagesActivity.start(mContext, mUserProfile.get_id(), mUserProfile.getUsername(), mUserProfile.getAvailability());
                    break;
                default: //case 0: default case
                    UtherProfileActivity.start(mContext, mUserProfile.get_id());
                    break;
            }
        }
    }
}
