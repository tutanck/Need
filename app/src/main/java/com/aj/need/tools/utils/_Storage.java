package com.aj.need.tools.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Created by joan on 21/10/2017.
 */

public class _Storage {

    public static String getRef(String uid) {
        return "users/" + uid + "/images/pp.jpg";
    }


    public static Task<Uri> loadRef(String imgRef) {
        return FirebaseStorage.getInstance().getReference().child(imgRef).getDownloadUrl();
    }


}
