package com.aj.need.domain.components.profile;

import android.app.Activity;
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
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;

/**
 * Created by joan on 21/09/17.
 */
public class UserProfilesRecyclerAdapter extends RecyclerView.Adapter<UserProfilesRecyclerAdapter.ViewHolder> {

    private final static String TAG = "UserProfilesRA";

    private List<UserProfile> mProfiles;
    private Context mContext;
    private int mOnClickListenerType;

    private RequestManager glide;

    public UserProfilesRecyclerAdapter(
            Context context,
            List<UserProfile> profiles,
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
        holder.bindItem(mProfiles.get(position));
    }

    //// TODO: 17/11/2017 test: not sure if the right way to do this
    @Override
    public void onViewDetachedFromWindow(ViewHolder viewHolder) {
        Log.d(TAG, "bf listenerRegistration" + viewHolder.listenerRegistration);
        viewHolder.unsync();
        Log.d(TAG, "af listenerRegistration" + viewHolder.listenerRegistration);
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }


    /*Avoid image duplication between rows
    * https://stackoverflow.com/questions/36240878/recyclerview-duplicated-items-on-scroll*/
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final static String TAG = "UserProfilesRA_VH";

        private ImageView userProfileIV;
        private TextView usernameTV;
        private FloatingActionButton userStatusFAB;
        private RatingBar userReputationRBar;
        private TextView userDistanceTV;//// TODO: 17/11/2017
        private TextView messageTV;
        private TextView messageDateTV;


        private UserProfile mUserProfile;
        //Potentially missing parts
        private String username = null;
        private Long reputation = Long.valueOf(0);
        private int availability = Avail.UNKNOWN;

        private ListenerRegistration listenerRegistration;


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


        public void bindItem(UserProfile userProfile) {
            _Storage.loadRef(_Storage.getRef(userProfile.get_id()))
                    .addOnSuccessListener((Activity) mContext, new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //@see https://github.com/bumptech/glide/issues/803
                            glide.load(uri)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(userProfileIV);
                        }
                    })
                    .addOnFailureListener((Activity) mContext, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            userProfileIV.setImageResource(R.drawable.ic_person_24dp);
                        }
                    });

            this.mUserProfile = userProfile;
            setLastMessage();
            setProfile();

            sync();

            userProfileIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtherProfileActivity.start(mContext, mUserProfile.get_id());
                }
            });
        }


        private void sync() {
            Log.d(TAG, "sync called");
            if (listenerRegistration != null) return;

            unsync();
            listenerRegistration = USERS.getUserRef(mUserProfile.get_id()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot userDoc, FirebaseFirestoreException e) {
                    Log.i(TAG, "onEvent:" + " userDoc=" + userDoc + " e=", e);

                    if (e == null && userDoc != null && userDoc.exists()) {
                        availability = userDoc.getLong(USERS.availabilityKey).intValue();
                        username = userDoc.getString(USERS.usernameKey);
                        reputation = userDoc.getLong(USERS.avgRatingKey);
                        setProfile();
                    }
                }
            });
        }

        synchronized void unsync() {
            if (listenerRegistration != null) listenerRegistration.remove();
            listenerRegistration = null;
        }


        private void setProfile() {
            mUserProfile.setAvailability(availability);
            mUserProfile.setUsername(username);
            mUserProfile.setReputation(reputation);

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

            String msgTxt = mContact.getMessage();

            if (IO.isCurrentUser(mContact.getFrom()))
                msgTxt = "Vous: " + msgTxt;

            messageTV.setText(msgTxt);
            messageDateTV.setText(_DateUtils.since(mContact.getDate()));

            boolean toRead = (!IO.isCurrentUser(mContact.getFrom()) && !mContact.isRead());
            int txtTypeFace = toRead ? Typeface.BOLD_ITALIC : Typeface.NORMAL;

            usernameTV.setTypeface(usernameTV.getTypeface(), txtTypeFace);

            messageTV.setTypeface(messageTV.getTypeface(), txtTypeFace);
            messageTV.setTextColor(ContextCompat.getColor(mContext, toRead ? R.color.Black : R.color.Silver));

            messageDateTV.setTypeface(messageDateTV.getTypeface(), txtTypeFace);
            messageDateTV.setTextColor(ContextCompat.getColor(mContext, toRead ? R.color.Black : R.color.Gray));
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
