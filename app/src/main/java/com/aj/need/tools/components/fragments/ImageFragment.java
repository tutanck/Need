package com.aj.need.tools.components.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aj.need.R;
import com.aj.need.main.App;
import com.aj.need.tools.utils.DatabaseHelper;
import com.aj.need.tools.utils._Bitmap;
import com.aj.need.tools.utils.__;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class ImageFragment extends Fragment {

    public static final int PICK_IMAGE_REQUEST = 1;

    private static final String DEFAULT_DRAWABLE_ID = "DEFAULT_DRAWABLE_ID";
    private static final String IMG_REF_STR = "IMG_REF_STR";
    private static final String EDITABLE = "EDITABLE";

    private App app;

    private RequestManager glide;

    private StorageReference imageRef;

    private ImageView imageView;

    private ProgressBarFragment progressBarFragment;

    private DatabaseHelper databaseHelper;


    public static ImageFragment newInstance(
            String imgRefStr
            , int defaultDrawableID
            , boolean editable
    ) {
        Bundle args = new Bundle();
        args.putString(IMG_REF_STR, imgRefStr);
        args.putInt(DEFAULT_DRAWABLE_ID, defaultDrawableID);
        args.putBoolean(EDITABLE, editable);

        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        glide = Glide.with(this);

        app = (App) (getActivity().getApplication());

        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        imageRef = storageRef.child(getArguments().getString(IMG_REF_STR));

        FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_image_view, container, false);

        progressBarFragment = (ProgressBarFragment) getChildFragmentManager().findFragmentById(R.id.waiter_modal_fragment);
        databaseHelper = new DatabaseHelper(getContext());

        imageView = layout.findViewById(R.id.imageView);
        imageView.setImageResource(getArguments().getInt(DEFAULT_DRAWABLE_ID));

        if (getArguments().getBoolean(EDITABLE))
            imageView.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                        }
                    }
            );

        return layout;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST)
            if (resultCode == RESULT_OK && data != null && data.getData() != null)
                upload(data.getData());
    }


    private void upload(final Uri localUri) {
        try {
            __.showLongToast(getContext(),getString(R.string.image_upload_in_progress));
            imageView.setClickable(false); //!important due to the progressBar that don't show up sometimes
            progressBarFragment.show();
            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localUri);

            UploadTask uploadTask = imageRef.putBytes(_Bitmap.getBytes(bitmap));
            uploadTask.addOnFailureListener(getActivity()/*!important*/, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressBarFragment.hide();
                    imageView.setClickable(true);
                    __.showShortToast(getContext(), getString(R.string.fail_to_upload_image_message));
                }
            }).addOnSuccessListener(getActivity()/*!important*/, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri remoteUri = taskSnapshot.getDownloadUrl();
                    app.setImageUri(imageRef.toString(), remoteUri);
                    //loadImg(localUri);//// TODO: 14/11/2017 conc uncoment
                    imageView.setClickable(true);
                    __.showLongToast(getContext(),"upload : End");
                }
            });

        } catch (IOException e) {
            __.showShortToast(getActivity(), getString(R.string.fail_to_retrieve_image_message));
            e.printStackTrace();
        }
    }


    private void loadImg(Uri uri) {
        __.showShortToast(getContext(),"loadImg: begins");
        glide.load(uri).listener(
                new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBarFragment.hide();
                        __.showShortToast(getContext(),"loadImg: onLoadFailed");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBarFragment.hide();
                        __.showShortToast(getContext(),"loadImg: onResourceReady");
                        return false;
                    }
                }
        ).into(imageView);
    }


    private void refreshImg() {
        progressBarFragment.show();
        Uri storedUri = app.getImageUri(imageRef.toString());
        if (storedUri == null) {
            imageRef.getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            loadImg(uri);
                            app.setImageUri(imageRef.toString(), uri);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBarFragment.hide();
                        }
                    });
            Log.d("ImageFragment", "use of the retrieved uri.");
        } else {
            loadImg(storedUri);
            Log.d("ImageFragment", "use of the stored uri=" + storedUri);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        __.showShortToast(getContext(),"onResume");
        refreshImg(); //// TODO: 14/11/2017  conc with onActResult
    }
}