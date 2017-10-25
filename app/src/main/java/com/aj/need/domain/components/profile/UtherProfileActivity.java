package com.aj.need.domain.components.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.aj.need.R;
import com.aj.need.db.colls.USER_CONTACTS;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class UtherProfileActivity extends AppCompatActivity {

    private static String UTHER_ID = "UTHER_ID";

    private String uther_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uther_profile);

        uther_id = getIntent().getStringExtra(UTHER_ID);

        if (savedInstanceState == null) //no duplicated fragments
            getSupportFragmentManager().beginTransaction().add(
                            R.id.profile_fragment_container
                            , ProfileFragment.newInstance(uther_id, false), "profile_fragment")
                    .commit();
    }

    public static void start(Context context, String id) {
        Intent intent = new Intent(context, UtherProfileActivity.class);
        intent.putExtra(UTHER_ID, id);
        context.startActivity(intent);
    }
}
