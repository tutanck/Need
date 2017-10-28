package com.aj.need.domain.components.keywords;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.aj.need.R;

import java.util.List;

/**
 * Created by joan on 21/09/17.
 */
public class UserKeywordsRecyclerAdapter extends RecyclerView.Adapter<UserKeywordsRecyclerAdapter.ViewHolder> {

    private List<UserKeyword> mUserKeywords;
    private Context mContext;
    private boolean mIsEditable;

    public UserKeywordsRecyclerAdapter(
            Context context,
            List<UserKeyword> userKeywords,
            boolean isEditable
    ) {
        mContext = context;
        mUserKeywords = userKeywords;
        mIsEditable = isEditable;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_user_keyword, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(mUserKeywords.get(position), mContext, mIsEditable);
    }

    @Override
    public int getItemCount() {
        return mUserKeywords.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;
        private Switch mSwitch;

        private boolean haveSwitchListener = false;

        private UserKeyword mUserKeyword;

        private Context mContext;

        private boolean mIsEditable;


        public ViewHolder(View v) {
            super(v);

            mTextView = v.findViewById(R.id.keyword_textview);
            mSwitch = v.findViewById(R.id.keyword_switch);
        }

        public void bindItem(UserKeyword userKeyword, final Context context, boolean isEditable) {
            this.mUserKeyword = userKeyword;
            this.mContext = context;
            this.mIsEditable = isEditable;

            mTextView.setText(userKeyword.getKeyword());
            mSwitch.setChecked(userKeyword.isActive());
            mSwitch.setClickable(mIsEditable);

            if (mIsEditable) //!importants
                if (!haveSwitchListener) {
                    mSwitch.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((UserKeywordsActivity) context).saveKeyword
                                    (mUserKeyword.getKeyword(), !mUserKeyword.isActive(), false, true);
                        }
                    });

                    haveSwitchListener = true;
                }
        }

        void deleteKeyword() {
            if (mIsEditable)
                ((UserKeywordsActivity) mContext).saveKeyword(mUserKeyword.getKeyword(), mUserKeyword.isActive(), true, true);
        }

    }
}
