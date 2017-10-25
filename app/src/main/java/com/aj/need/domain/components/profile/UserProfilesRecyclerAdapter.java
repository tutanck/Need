package com.aj.need.domain.components.profile;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.domain.components.messages.MessagesActivity;
import com.aj.need.tools.utils.Avail;
import com.aj.need.tools.utils._Storage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;

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

            usernameTV.setText(mUserProfile.getUsername());
            userStatusFAB.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor
                    (context, Avail.getColor(mUserProfile.getAvailability()))));

            userReputationRBar.setRating(mUserProfile.getReputation());
            messageTV.setText(mUserProfile.getLastMessage());
            messageDateTV.setText(mUserProfile.getLastMessageDate());

            userProfileIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtherProfileActivity.start(mContext, mUserProfile.get_id());
                }
            });

            _Storage.loadRef(_Storage.getRef(userProfile.get_id()))
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //todo @see https://github.com/bumptech/glide/issues/803 and try @AllanWang sol if pb :
                            //I faced the same issue .. i fixed it like that :Glide.with(mContext.getApplicationContext()) //activity.getApplicationContext()
                            //from @AllanWang : @tutanck I think that removes the whole life cycle handling. Best I initialize it with the activity context on creation or validate beforehand
                            Glide.with(mContext.getApplicationContext()/*!important*/) //fix of : Glide's Fatal Exception: java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity
                                    .load(uri)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(userProfileIV);
                        }
                    });
        }


        @Override
        public void onClick(View view) {
            switch (mOnClickListenerType) {
                case 0:
                    UtherProfileActivity.start(mContext, mUserProfile.get_id());
                    break;
                case 1:
                    MessagesActivity.start(mContext, mUserProfile.get_id(), mUserProfile.getUsername());
                    break;
                default:
                    throw new RuntimeException("UserProfilesRecyclerAdapter/ViewHolder::onClick: Unknown mOnClickListenerType " + mOnClickListenerType);
            }
        }
    }
}
