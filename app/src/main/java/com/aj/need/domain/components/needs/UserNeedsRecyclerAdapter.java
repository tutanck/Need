package com.aj.need.domain.components.needs;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.colls.APPLICANTS;
import com.aj.need.db.colls.USER_NEEDS;
import com.aj.need.tools.components.services.ComponentsServices;
import com.aj.need.tools.utils.Tagger;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * Created by joan on 21/09/17.
 */
public class UserNeedsRecyclerAdapter extends RecyclerView.Adapter<UserNeedsRecyclerAdapter.ViewHolder> {

    private List<UserNeed> mUserNeeds;
    private Context mContext;

    public UserNeedsRecyclerAdapter(
            Context context,
            List<UserNeed> userNeeds
    ) {
        mContext = context;
        mUserNeeds = userNeeds;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_user_need, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(mUserNeeds.get(position), mContext);
    }

    @Override
    public int getItemCount() {
        return mUserNeeds.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private Context mContext;
        private LinearLayout needWrapperLayout;
        private TextView mTitleTextView, mSearchTextView, mNbPokesTextView;
        private ImageView mPokesIV;
        private FloatingActionButton fabNeedStatus;

        private UserNeed mUserNeed;

        public UserNeed getUserNeed() {
            return mUserNeed;
        }

        public ViewHolder(View v) {
            super(v);
            needWrapperLayout = v.findViewById(R.id.needWrapperLayout);
            mTitleTextView = v.findViewById(R.id.need_title_tv);
            mSearchTextView = v.findViewById(R.id.need_search_tv);
            mNbPokesTextView = v.findViewById(R.id.need_nb_pokes_tv);
            mPokesIV = v.findViewById(R.id.need_nb_pokes_IV);
            fabNeedStatus = v.findViewById(R.id.fab_need_status);
        }

        public void bindItem(UserNeed userNeed, Context context) {
            this.mContext = context;
            this.mUserNeed = userNeed;

            mTitleTextView.setText(userNeed.getTitle());
            mSearchTextView.setText(Tagger.tags(userNeed.getSearch()));
            fabNeedStatus.setBackgroundTintList(
                    ColorStateList.valueOf
                            (ContextCompat.getColor
                                    (context, userNeed.isActive() ? R.color.Lime : R.color.Red))
            );


            APPLICANTS.getAdApplicantsRef(mUserNeed.getOwnerID(), mUserNeed.get_id()).get().addOnSuccessListener(
                    new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot querySnapshot) {
                            int nbActiveApplicants = 0;
                            for (DocumentSnapshot doc : querySnapshot) {
                                Boolean active = doc.getBoolean(APPLICANTS.activeKey);
                                //// TODO: 07/11/2017  rem verif  active != null in prod mod. All must always have an active status
                                if (active != null && active)
                                    nbActiveApplicants++;
                            }
                            setApplicants(nbActiveApplicants);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("bindItem/", "UserNeedsRecyclerAdapter::onFailure", e);
                }
            });

            ComponentsServices.setSelectable(mContext,needWrapperLayout);
        }


        void deleteNeed(final Activity contextActivity, final UserNeedsFragment delegate, final String userID, final List<UserNeed> userNeeds, final UserNeedsRecyclerAdapter adapter) {
            USER_NEEDS.getCurrentUserNeedsRef().document(mUserNeed.get_id()).update(USER_NEEDS.deletedKey, true)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            __.showShortToast(contextActivity, contextActivity.getString(R.string.error_deleting_user_need_message));
                        }
                    });
        }


        private void setApplicants(int nbActiveApplicants) {
            int color = ContextCompat.getColor(mContext
                    , nbActiveApplicants > 0 ? R.color.Black : R.color.DarkGray);

            mNbPokesTextView.setText(Integer.toString(nbActiveApplicants));
            mNbPokesTextView.setTextColor(color);
            mNbPokesTextView.setVisibility(View.VISIBLE);
        }

    }
}
