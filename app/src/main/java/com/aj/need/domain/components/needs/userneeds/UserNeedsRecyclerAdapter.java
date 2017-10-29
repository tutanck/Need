package com.aj.need.domain.components.needs.userneeds;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.colls.USER_NEEDS;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnFailureListener;

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

        private TextView mTitleTextView;
        private TextView mSearchTextView;
        private TextView mNbPokesTextView;
        private FloatingActionButton fabNeedStatus;

        private UserNeed mUserNeed;

        public UserNeed getUserNeed() {
            return mUserNeed;
        }

        public ViewHolder(View v) {
            super(v);
            mTitleTextView = v.findViewById(R.id.need_title_textview);
            mSearchTextView = v.findViewById(R.id.need_search_textview);
            mNbPokesTextView = v.findViewById(R.id.need_nb_pokes_textview);
            fabNeedStatus = v.findViewById(R.id.fab_need_status);
        }

        public void bindItem(UserNeed userNeed, Context context) {
            this.mUserNeed = userNeed;

            mTitleTextView.setText(userNeed.getTitle());
            mSearchTextView.setText(userNeed.getSearch());
            fabNeedStatus.setBackgroundTintList(
                    ColorStateList.valueOf
                            (ContextCompat.getColor
                                    (context, userNeed.isActive() ? R.color.Lime : R.color.Red))
            );
            //// TODO: 24/09/2017 nb pokes
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
    }
}
