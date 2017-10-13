package com.aj.need.domain.components.keywords;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.aj.need.R;
import com.aj.need.db.colls.USER_KEYWORDS;
import com.aj.need.domain.components.needs.main.UserNeedsRecyclerAdapter;
import com.aj.need.main.A;
import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.regina.ack.UIAck;
import com.aj.need.tools.utils.__;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserKeywordsActivity extends AppCompatActivity {

    private List<UserKeyword> mUserKeywords = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private UserKeywordsRecyclerAdapter mAdapter;

    private EditText etKeyword;
    private ImageButton btnAdd;

    private LinearLayout indicationsLayout;
    private ProgressBarFragment progressBarFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_keywords);

        btnAdd = (ImageButton) findViewById(R.id.add_keyword_button);
        btnAdd.setEnabled(false);

        etKeyword = (EditText) findViewById(R.id.add_keyword_input);

        mRecyclerView = (RecyclerView) findViewById(R.id.keywords_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new UserKeywordsRecyclerAdapter(UserKeywordsActivity.this, mUserKeywords);
        mRecyclerView.setAdapter(mAdapter);

        indicationsLayout = (LinearLayout) findViewById(R.id.activity_user_keywords_indications);
        progressBarFragment = (ProgressBarFragment) getSupportFragmentManager().findFragmentById(R.id.waiter_modal_fragment);
        progressBarFragment.setBackgroundColor(Color.TRANSPARENT);

        functionalizeETKeyword();
        functionalizeBtnAdd();
        setRecyclerViewItemTouchListener();
    }


    public static void start(Context context) {
        context.startActivity(new Intent(context, UserKeywordsActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadKeywords();
    }


    private void loadKeywords() {
        progressBarFragment.show();
        USER_KEYWORDS.loadUserKeywords(A.user_id(this), new UIAck(this) {
            @Override
            protected void onRes(Object res, JSONObject ctx) {
                try {
                    JSONArray jar = (JSONArray) res;
                    mUserKeywords.clear();
                    int i = 0;
                    for (; i < jar.length(); i++) {
                        JSONObject jo = jar.getJSONObject(i);
                        mUserKeywords.add(new UserKeyword(
                                jo.getString(USER_KEYWORDS.keywordKey)
                                , jo.getBoolean(USER_KEYWORDS.activeKey)));
                    }

                    indicationsLayout.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
                    mAdapter.notifyDataSetChanged();
                    progressBarFragment.hide();
                } catch (JSONException e) {
                    __.fatal(e); //SNO : if a doc exist the keyword field should exist too
                }
            }
        });
    }


    void saveKeyword(String keyword, boolean active, boolean deleted) {
        if (isKeyword(keyword)) {
            progressBarFragment.show();
            USER_KEYWORDS.saveUserKeyword(keyword, A.user_id(this), active, deleted,
                    new UIAck(this) {
                        @Override
                        protected void onRes(Object res, JSONObject ctx) {
                            etKeyword.setText("");
                            loadKeywords();
                        }

                        @Override
                        protected void onErr(JSONObject err, JSONObject ctx) {
                            super.onErr(err, ctx);
                            progressBarFragment.hide();
                        }
                    });
        } else
            __.showLongSnack(btnAdd, "Un mot-clé est composé d'un seul mot (caractères alphanumériques sans accents).");

    }


    private void functionalizeBtnAdd() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveKeyword(etKeyword.getText().toString().trim(), true, false);
            }
        });
    }


    private boolean isKeyword(String input) {
        return !TextUtils.isEmpty(input) && !__.found("[^a-zA-Z0-9]", input);
    }


    private void functionalizeETKeyword() {
        etKeyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnAdd.setEnabled(etKeyword.getText().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void setRecyclerViewItemTouchListener() {
        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {

                AlertDialog.Builder builder = new AlertDialog.Builder(UserKeywordsActivity.this);
                builder.setTitle("Supprimer le mot clé ?");
                builder.setMessage(getString(R.string.delete_keyword_warning));

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        progressBarFragment.show();
                        ((UserKeywordsRecyclerAdapter.ViewHolder) viewHolder).deleteKeyword();
                    }
                });

                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mAdapter.notifyDataSetChanged();
                    }
                });

                builder.show();


            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

}
