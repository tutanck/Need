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
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST)
            if (resultCode == RESULT_OK && data != null && data.getData() != null)
                upload(data.getData());
    }


    private void upload(final Uri localUri) {
        try {
            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), localUri);

            progressBarFragment.show();
            UploadTask uploadTask = imageRef.putBytes(_Bitmap.getBytes(bitmap));
            uploadTask.addOnFailureListener(getActivity()/*!important*/, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressBarFragment.hide();
                    __.showShortToast(getContext(), getString(R.string.fail_to_upload_image_message));
                }
            }).addOnSuccessListener(getActivity()/*!important*/, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri remoteUri = taskSnapshot.getDownloadUrl();
                    app.setImageUri(imageRef.toString(), remoteUri);
                    loadImg(localUri); //less secure (unsafe uri) but can use the same transforms used to reload the image.

                    //imageView.setImageBitmap(bitmap);  //more secure (safe existing img) but can use the same transform used to reload the image.
                    // progressBarFragment.hide();
                }
            });

        } catch (IOException e) {
            __.showShortToast(getActivity(), getString(R.string.fail_to_retrieve_image_message));
            e.printStackTrace();
        }
    }


    private void loadImg(Uri uri) {
        glide.load(uri).listener(
                new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBarFragment.hide();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBarFragment.hide();
                        return false;
                    }
                }
        ).into(imageView);
    }


    @Override
    public void onStart() {
        super.onStart();
        refreshImg();
    }


    // TODO: 09/10/2017 Optimize: shouldnt always reload img see how to store img locally : do it for all the ProfileFragment iof this fragment
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


        //// TODO: 10/11/2017  rem after deciding if usefull to show progressbar
        /*progressBarFragment.show();
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = _Bitmap.getImage(bytes);
                imageView.setImageBitmap(bitmap);
                progressBarFragment.hide();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressBarFragment.hide();
               /* todo see what to do (could be abusive and disturbing for the user)
               int errCode = ((StorageException) exception).getErrorCode();
                if (errCode != StorageException.ERROR_OBJECT_NOT_FOUND)
                    __.showShortToast(getContext(), "Erreur de chargement de l'image.");*/
        /*    }
        });*/
    }


}