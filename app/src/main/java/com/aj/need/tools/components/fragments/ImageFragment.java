package com.aj.need.tools.components.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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


    //// TODO: 30/10/2017 redo 
    //// TODO: 30/10/2017 @see https://futurestud.io/tutorials/glide-advanced-loading
    private void upload(final Uri uri) {
        try {
            final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            UploadTask uploadTask = imageRef.putBytes(_Bitmap.getBytes(bitmap));

            progressBarFragment.show();

            uploadTask.addOnFailureListener(getActivity()/*!important*/, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    progressBarFragment.hide();
                    __.showShortToast(getContext(), getString(R.string.fail_to_upload_image_message));
                }
            }).addOnSuccessListener(getActivity()/*!important*/, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    app.setImageUri(imageRef.toString(), uri);
                    imageView.setImageBitmap(bitmap);
                    progressBarFragment.hide();
                }
            });

        } catch (IOException e) {
            __.showShortToast(getActivity(), getString(R.string.fail_to_retrieve_image_message));
            e.printStackTrace();
        }
    }

    //// TODO: 30/10/2017 redo
    // TODO: 09/10/2017 Optimize: shouldnt always reload img see how to store img locally : do it for all the ProfileFragment iof this fragment
    private void refreshImg() {
        progressBarFragment.show();
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
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        refreshImg();
    }
}