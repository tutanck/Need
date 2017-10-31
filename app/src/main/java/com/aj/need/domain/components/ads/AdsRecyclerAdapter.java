package com.aj.need.domain.components.ads;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.domain.components.needs.userneeds.UserNeed;
import com.aj.need.domain.components.needs.userneeds.UserNeedsRecyclerAdapter;
import com.aj.need.domain.components.profile.UtherProfileActivity;
import com.aj.need.tools.components.fragments.DatePanelFragment;
import com.aj.need.tools.components.fragments.DatePickerFragment;
import com.aj.need.tools.components.services.ComponentsServices;
import com.aj.need.tools.utils.Tagger;
import com.aj.need.tools.utils._Storage;
import com.aj.need.tools.utils.__;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

/**
 * Created by joan on 21/09/17.
 */
public class AdsRecyclerAdapter extends RecyclerView.Adapter<AdsRecyclerAdapter.ViewHolder> {

    private List<UserNeed> mAdList;
    private Context mContext;

    public AdsRecyclerAdapter(
            Context context,
            List<UserNeed> adList
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

        private Context mContext;

        private LinearLayout ownerProfileLayout, detailsLayout;
        private ImageView ownerIV;
        private TextView mOwnerNameTV, mAdDateTV, mAdLocationTV, mDescriptionTV, mkeywordsTV;

        private UserNeed mAd;


        public ViewHolder(View v) {
            super(v);
            ownerProfileLayout = v.findViewById(R.id.ownerProfileLayout);
            detailsLayout = v.findViewById(R.id.detailsLayout);
            ownerIV = v.findViewById(R.id.ownerIV);
            mOwnerNameTV = v.findViewById(R.id.ownerNameTV);
            mAdDateTV = v.findViewById(R.id.adDateTV);
//            mAdLocationTV = v.findViewById(R.id.adLocationTV);
            mDescriptionTV = v.findViewById(R.id.descriptionTV);
            mkeywordsTV = v.findViewById(R.id.keywordsTV);
        }

        public void bindItem(UserNeed ad, Context context) {
            this.mAd = ad;
            this.mContext = context;



          /*  mOwnerNameTV.setText(ad.getOwnerID());
            mAdDateTV.setText(_DateUtils.since(ad.getDate()));
//            mAdLocationTV.setText(ad.getWhere());
            mDescriptionTV.setText(ad.getDescription());
            mkeywordsTV.setText(Tagger.tags(ad.getSearch()));*/


            _Storage.loadRef(_Storage.getRef(mAd.getOwnerID()))
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //todo @see https://github.com/bumptech/glide/issues/803 and try @AllanWang sol if pb : init Glide with the mContext asap (in the UserProfilesRecyclerAdapter's constructor of if possible even sooner)
                            //I faced the same issue .. i fixed it like that :Glide.with(mContext.getApplicationContext()) //activity.getApplicationContext()
                            //from @AllanWang : @tutanck I think that removes the whole life cycle handling. Best I initialize it with the activity context on creation or validate beforehand
                            Glide.with(mContext.getApplicationContext()/*!important*/) //fix of : Glide's Fatal Exception: java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity
                                    .load(uri)
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(ownerIV);
                        }
                    });

            ComponentsServices.setSelectable(mContext, ownerProfileLayout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtherProfileActivity.start(mContext, mAd.getOwnerID());
                }
            });


            ComponentsServices.setSelectable(mContext, detailsLayout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.sample_text);
                    builder.setMessage(mContext.getString(R.string.sample_text) + "\n\nMot-clés:\n" + Tagger.tags(mContext.getString(R.string.sample_text)));

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

                    builder.show();
                }
            });


            //// TODO: 31/10/2017 replace for the meta date if avail
            ComponentsServices.setSelectable(mContext, mAdDateTV, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mAd.getMetaWhenTime() == null) return;
                    DatePanelFragment fragment = DatePanelFragment.newFrozenInstance(mAd.getMetaWhenTime());
                    fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "Date dialog");
                }
            });


        }

    }
}
