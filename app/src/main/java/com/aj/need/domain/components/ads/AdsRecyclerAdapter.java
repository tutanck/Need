package com.aj.need.domain.components.ads;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aj.need.R;

import java.util.List;

/**
 * Created by joan on 21/09/17.
 */
public class AdsRecyclerAdapter extends RecyclerView.Adapter<AdsRecyclerAdapter.ViewHolder> {

    private List<Ad> mAdList;
    private Context mContext;

    public AdsRecyclerAdapter(
            Context context,
            List<Ad> adList
    ) {
        mContext = context;
        mAdList = adList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_item_ad, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(mAdList.get(position), mContext);
    }

    @Override
    public int getItemCount() {
        return mAdList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mOwnerNameTV;
        private TextView mAdDateTV;
        private TextView mAdLocationTV;
        private TextView mDescriptionTV;

        private Ad mAd;

        public Ad getUserNeed() {
            return mAd;
        }

        public ViewHolder(View v) {
            super(v);
            mOwnerNameTV = v.findViewById(R.id.ownerNameTV);
            mAdDateTV = v.findViewById(R.id.adDateTV);
            mAdLocationTV = v.findViewById(R.id.adLocationTV);
            mDescriptionTV = v.findViewById(R.id.descriptionTV);
        }

        public void bindItem(Ad ad, Context context) {
            this.mAd = ad;

           //// TODO: 08/10/2017
            
        }
    }
}
