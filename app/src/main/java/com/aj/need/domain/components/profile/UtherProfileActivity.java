package com.aj.need.domain.components.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.aj.need.R;
import com.aj.need.domain.components.messages.MessagesActivity;
import com.aj.need.tools.components.fragments.IDKeyFormField;
import com.aj.need.tools.components.fragments.ImageFragment;
import com.aj.need.tools.components.services.FormFieldKindTranslator;

public class UtherProfileActivity extends AppCompatActivity {

    private static String UTHER = "UTHER";

    private String uther_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uther_profile);

        uther_id = getIntent().getStringExtra(UTHER);

        if (savedInstanceState == null) //no duplicated fragments
            getSupportFragmentManager().beginTransaction().add(R.id.profile_fragment_container
                    , ProfileFragment.newInstance(uther_id, false), "profile_fragment").commit();
    }

    public static void start(Context context, String _id) {
        Intent intent = new Intent(context, UtherProfileActivity.class);
        intent.putExtra(UTHER, _id);
        context.startActivity(intent);
    }
}
