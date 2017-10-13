package com.aj.need.domain.components.keywords;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.aj.need.R;
import com.aj.need.db.colls.USER_KEYWORDS;
import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.domain.components.profile.UtherProfileActivity;
import com.aj.need.domain.entities.User;
import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.regina.ack.UIAck;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UtherKeywordsActivity extends AppCompatActivity {

    private final static String USER_ID = "USER_ID";

    private ListView mListView;
    private ArrayAdapter adapter;

    private LinearLayout indicationsLayout;
    private ProgressBarFragment progressBarFragment;

    List<String> keywords = new ArrayList<>();

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.component_list_view);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        mListView = (ListView) findViewById(R.id.list_view);

        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, keywords);
        mListView.setAdapter(adapter);

        progressBarFragment = (ProgressBarFragment) getSupportFragmentManager().findFragmentById(R.id.waiter_modal_fragment);
        progressBarFragment.setBackgroundColor(Color.TRANSPARENT);

        indicationsLayout = (LinearLayout) findViewById(R.id.component_list_indications);
    }


    @Override
    protected void onStart() {
        super.onStart();
        loadKeywords();

//// TODO: 13/10/2017 rem 
      /*  progressBarFragment.show(); 
        USER_KEYWORDS.loadUtherKeywords(getIntent().getStringExtra(USER_ID)
                , new UIAck(this) {
                    @Override
                    protected void onRes(Object res, JSONObject ctx) {
                        try {
                            JSONArray jar = (JSONArray) res;
                            adapter.clear();
                            int i = 0;
                            for (; i < jar.length(); i++)
                                adapter.add(jar.getJSONObject(i).getString(USER_KEYWORDS.keywordKey));

                            indicationsLayout.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
                            progressBarFragment.hide();

                        } catch (JSONException e) {
                            __.fatal(e); //SNO : if a doc exist the keyword field should exist too
                        }
                    }
                });*/
    }


    private void loadKeywords() {
        progressBarFragment.show();

        db.collection(User.coll).document(getIntent().getStringExtra(USER_ID))
                .collection(UserKeyword.coll).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            adapter.clear();
                            for (DocumentSnapshot keywordDoc : task.getResult())
                                adapter.add(keywordDoc.getId());

                            indicationsLayout.setVisibility(keywords.size() == 0 ? View.VISIBLE : View.GONE);
                            adapter.notifyDataSetChanged();
                            progressBarFragment.hide();
                        } else
                            __.showShortToast(UtherKeywordsActivity.this, "Impossible de charger les mots clés");

                    }
                });
    }

    public static void start(Context context, String user_id) {
        Intent intent = new Intent(context, UtherKeywordsActivity.class);
        intent.putExtra(USER_ID, user_id);
        context.startActivity(intent);
    }


}
