package com.aj.need.domain.components.needs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.aj.need.R;
import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.domain.components.profile.UserProfilesRecyclerAdapter;
import com.aj.need.tools.utils.__;

import java.util.ArrayList;


public class UserNeedNewSearchActivity extends AppCompatActivity {

    private EditText searchET;
    private ImageButton searchBtn;

    private ArrayList<UserProfile> mProfiles = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private UserProfilesRecyclerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_need_new_search);

        searchET = (EditText) findViewById(R.id.need_search_bar_et);

        searchBtn = (ImageButton) findViewById(R.id.need_search_bar_btn);
        searchBtn.setEnabled(false);

        mRecyclerView = (RecyclerView) findViewById(R.id.found_profiles_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new UserProfilesRecyclerAdapter(this, mProfiles);
        mRecyclerView.setAdapter(mAdapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_open_need_save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = searchET.getText().toString().trim();

                if (TextUtils.isEmpty(searchText))
                    __.showShortSnack(view, "Impossible de publier une recherche vide!");
                else
                    UserNeedSaveActivity.start(UserNeedNewSearchActivity.this, searchText,false);
            }
        });

        functionalizeSearchBtn();
        functionalizeSearchET();
    }


    private void functionalizeSearchBtn() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                __.showShortToast(UserNeedNewSearchActivity.this, "searching...");
            }
        });
    }


    private void functionalizeSearchET() {
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchBtn.setEnabled(searchET.getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    public static void start(Context context) {
        context.startActivity(new Intent(context, UserNeedNewSearchActivity.class));
    }

}
