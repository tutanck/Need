package com.aj.need.domain.components.profile;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
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
import com.aj.need.db.IO;
import com.aj.need.db.colls.USERS;
import com.aj.need.domain.components.messages.MessagesActivity;
import com.aj.need.tools.utils.Avail;
import com.aj.need.tools.utils._DateUtils;
import com.aj.need.tools.utils._Storage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

/**
 * Created by joan on 21/09/17.
 */
public class UserProfilesRecyclerAdapter extends RecyclerView.Adapter<UserProfilesRecyclerAdapter.ViewHolder> {

    private ArrayList<UserProfile> mProfiles;
    private Context mContext;
    private int mOnClickListenerType;

    private RequestManager glide;

    public UserProfilesRecyclerAdapter(
            Context context,
            ArrayList<UserProfile> profiles,
            int onClickListenerType,
            RequestManager glide
    ) {
        this.mContext = context;
        this.mProfiles = profiles;
        this.mOnClickListenerType = onClickListenerType;
        this.glide = glide;
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


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

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


            if (mUserProfile.isIncomplete()) {
                Log.d("bindItem/", "UserProfilesRecyclerAdapter::getUser");
                USERS.getUserRef(mUserProfile.get_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot userDoc) {
                        Log.d("bindItem/", "UserProfilesRecyclerAdapter::onSuccess: data=" + userDoc.getData());
                        if (userDoc.exists()) {
                            mUserProfile.setAvailability(userDoc.getLong(USERS.availabilityKey).intValue());
                            mUserProfile.setUsername(userDoc.getString(USERS.usernameKey));
                            mUserProfile.setReputation(userDoc.getLong(USERS.avgRatingKey));
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
                            //@see https://github.com/bumptech/glide/issues/803
                            glide.load(uri)
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
            if (!(mUserProfile instanceof Contact)) {
                messageTV.setText(null);
                messageDateTV.setText(null);
                return;
            }

            Contact mContact = (Contact) mUserProfile;
            messageTV.setText(mContact.getMessage());
            messageDateTV.setText(_DateUtils.since(mContact.getDate()));

            boolean toRead = ((!mContact.getFrom().equals(IO.getCurrentUserUid())) && (!mContact.isRead()));
            int txtColor = ContextCompat.getColor(mContext, toRead ? R.color.Black : R.color.Silver);
            int txtTypeFace = toRead ? Typeface.BOLD_ITALIC : Typeface.NORMAL;

            messageTV.setTypeface(messageTV.getTypeface(), txtTypeFace);
            messageTV.setTextColor(txtColor);

            messageDateTV.setTypeface(messageDateTV.getTypeface(), txtTypeFace);
            messageDateTV.setTextColor(txtColor);

            usernameTV.setTypeface(usernameTV.getTypeface(), txtTypeFace);
            usernameTV.setTextColor(txtColor);
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
