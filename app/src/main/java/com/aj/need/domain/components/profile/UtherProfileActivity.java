package com.aj.need.domain.components.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.aj.need.R;
import com.aj.need.db.colls.USER_CONTACTS;
import com.aj.need.tools.components.fragments.FormField;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class UtherProfileActivity extends AppCompatActivity implements FormField.Listener {

    private static String UTHER_ID = "UTHER_ID";
    private String uther_id;

    ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uther_profile);

        uther_id = getIntent().getStringExtra(UTHER_ID);

        if (savedInstanceState == null) { //no duplicated fragments
            profileFragment = ProfileFragment.newInstance(uther_id, false);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.profile_fragment_container
                            , profileFragment, "profile_fragment"
                    ).commit();
        }
    }


    @Override
    public void onFormFieldCreated(FormField formField) {
        profileFragment.onFormFieldCreated(formField);
    }


    public static void start(Context context, String _id) {
        Intent intent = new Intent(context, UtherProfileActivity.class);
        intent.putExtra(UTHER_ID, _id);
        context.startActivity(intent);
    }
}
