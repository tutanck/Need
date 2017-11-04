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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.db.colls.APPLICANTS;
import com.aj.need.domain.components.needs.userneeds.UserNeed;
import com.aj.need.main.App;
import com.aj.need.tools.components.fragments.DatePanelFragment;
import com.aj.need.tools.components.services.ComponentsServices;
import com.aj.need.tools.utils.Tagger;
import com.aj.need.tools.utils._DateUtils;
import com.aj.need.tools.utils._PlaceUtils;
import com.aj.need.tools.utils._Storage;
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

        private LinearLayout detailsLayout, publicationDateLayout, placeLayout;
        private ImageView ownerIV;
        private Button pokeBtn;
        private TextView mOwnerNameTV, mTitleTV, mAdDateTV, mAdDistanceTV, mDescriptionTV, mKeywordsTV;

        private UserNeed mAd;


        public ViewHolder(View v) {
            super(v);
            detailsLayout = v.findViewById(R.id.detailsLayout);
            placeLayout = v.findViewById(R.id.placeLayout);
            publicationDateLayout = v.findViewById(R.id.publicationDateLayout);

            ownerIV = v.findViewById(R.id.ownerIV);

            mOwnerNameTV = v.findViewById(R.id.ownerNameTV);
            mTitleTV = v.findViewById(R.id.titleTV);
            mDescriptionTV = v.findViewById(R.id.descriptionTV);
            mKeywordsTV = v.findViewById(R.id.keywordsTV);

            mAdDateTV = v.findViewById(R.id.adDateTV);
            mAdDistanceTV = v.findViewById(R.id.adDistanceTV);

            pokeBtn = v.findViewById(R.id.pokeBtn);
        }


        public void bindItem(UserNeed ad, Context context) {
            this.mAd = ad;
            this.mContext = context;

            mOwnerNameTV.setText(ad.getOwnerName());
            mTitleTV.setText(ad.getTitle());
            mAdDateTV.setText(_DateUtils.ago(ad.getDate()));
            mAdDistanceTV.setText(_PlaceUtils.distance(
                    ad.getMetaWhereCoord().getLatitude()
                    , ad.getMetaWhereCoord().getLongitude()
                    , 0, 0
            ));
            mDescriptionTV.setText(ad.getDescription());
            mKeywordsTV.setText(Tagger.tags(ad.getSearch()));

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


            ComponentsServices.setSelectable(mContext, detailsLayout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mAd.getTitle());
                    builder.setMessage(mAd.getDescription() + "\n\nMot-clés:\n" + Tagger.tags(mAd.getSearch()));
                    builder.setPositiveButton("Retour", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                }
            });


            ComponentsServices.setSelectable(mContext, publicationDateLayout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePanelFragment fragment = DatePanelFragment.newFrozenInstance(mAd.getDate().getTime());
                    fragment.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "Date dialog");
                }
            });


            ComponentsServices.setSelectable(mContext, placeLayout, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Lieu");
                    builder.setMessage(mAd.isMetaIsWhereVisible() ? mAd.getWhere() : mContext.getString(R.string.adr_is_not_visible_msg));
                    builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.show();
                }
            });


            pokeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Proposition de services");
                    builder.setMessage("Vous souhaitez proposer vos services pour l'annonce : \n'"+mAd.getTitle()+"'.");
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    builder.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            App app = ((App) ((Activity) mContext).getApplication());
                            APPLICANTS.getAdApplicantsRef(mAd.getOwnerID(), mAd.get_id())
                                    .document(IO.getCurrentUserUid()).set(app.getUser());
                        }
                    });
                    builder.show();
                }
            });

        }

    }
}
