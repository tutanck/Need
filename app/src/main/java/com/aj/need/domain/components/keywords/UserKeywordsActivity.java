package com.aj.need.domain.components.keywords;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.db.colls.USER_KEYWORDS;
import com.aj.need.tools.utils.Jarvis;
import com.aj.need.tools.utils.PatternsHolder;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserKeywordsActivity extends AppCompatActivity {

    private final static String TAG = "UserKeywordsAct", UID = "UID";

    private final static int MAX_KEYWORDS = 25;

    private String uid = null;


    //Refresh & indications
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout indicationsLayout;


    // UI:
    private RecyclerView mRecyclerView;
    private UserKeywordsRecyclerAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<UserKeyword> keywordList = new ArrayList<>();

    private Query mLoadQuery;
    private QuerySnapshot lastQuerySnapshot;


    private EditText etKeyword;
    private ImageButton btnAdd;

    private ListenerRegistration keywordsRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_keywords);

        uid = getIntent().getStringExtra(UID);

        if (uid == null) __.fatal("UserKeywordsActivity : uid == null");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(this));
        mAdapter = new UserKeywordsRecyclerAdapter(UserKeywordsActivity.this, keywordList, IO.isCurrentUser(uid));
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = findViewById(R.id.recycler_view_SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sync();
            }
        });


        indicationsLayout = findViewById(R.id.component_recycler_indications_layout);

        etKeyword = findViewById(R.id.add_keyword_input);

        btnAdd = findViewById(R.id.add_keyword_button);
        btnAdd.setEnabled(false);


        TextView indicationTV1 = findViewById(R.id.indicationTV1);
        TextView indicationTV2 = findViewById(R.id.indicationTV2);


        if (IO.isCurrentUser(uid)) {
            indicationTV1.setText(R.string.activity_keywords_indic1);
            indicationTV2.setText(R.string.activity_keywords_indic2);

            functionalizeETKeyword();
            functionalizeBtnAdd();
            setRecyclerViewItemTouchListener();
        } else {
            indicationTV1.setText(R.string.activity_keywords_indic3);
            indicationTV2.setText(R.string.activity_keywords_indic4);

            btnAdd.setVisibility(View.GONE);
            etKeyword.setVisibility(View.GONE);
        }

        mLoadQuery = USER_KEYWORDS.getUserKeywordsRef(getIntent().getStringExtra(UID))
                .whereEqualTo(USER_KEYWORDS.deletedKey, false)
                .orderBy(USER_KEYWORDS.activeKey, Query.Direction.DESCENDING)
                .orderBy(USER_KEYWORDS.keywordKey);
    }


    private synchronized void sync() {
        mSwipeRefreshLayout.setRefreshing(true);

        unsync();
        keywordsRegistration = mLoadQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                Log.w(TAG, "keywordsRegistration: " + "querySnapshot=" + querySnapshot + " error=" + e);

                if (e == null && querySnapshot != null) {
                    // refresh ui
                    refreshUserKeywordsList(querySnapshot);

                    //Indicate the sync's result status
                    indicationsLayout.setVisibility(keywordList.size() == 0 ? View.VISIBLE : View.GONE);

                    // Scroll the list back to the top.
                    mRecyclerView.scrollToPosition(0);
                } else
                    __.showShortToast(UserKeywordsActivity.this, getString(R.string.load_error_message));

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private synchronized void refreshUserKeywordsList(QuerySnapshot querySnapshot) {
        if (querySnapshot == null) return;
        lastQuerySnapshot = querySnapshot;

        keywordList.clear();
        keywordList.addAll(new Jarvis<UserKeyword>().tr(querySnapshot, new UserKeyword()));
        mAdapter.notifyDataSetChanged();
    }


    void saveKeyword(String keyword, boolean active, boolean deleted, boolean isUpdate) {
        if (isKeyword(keyword)) {
            USER_KEYWORDS.getCurrentUserKeywordsRef()
                    .document(keyword).set(new UserKeyword(keyword, active, deleted))
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            __.showShortToast(UserKeywordsActivity.this, getString(R.string.error_saving_keyword_message));
                        }
                    });
            if (!isUpdate) etKeyword.setText("");
        } else
            __.showLongSnack(btnAdd, getString(R.string.keyword_conformity_indication));
    }


    private void functionalizeBtnAdd() {
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keywordList.size() > MAX_KEYWORDS) {
                    __.showLongSnack(btnAdd, getString(R.string.you_can_have_max_keywords) + " " + MAX_KEYWORDS + " " + getString(R.string.keywords));
                    return;
                }
                saveKeyword(etKeyword.getText().toString().trim(), true, false, false);
            }
        });
    }


    private boolean isKeyword(String input) {
        return !TextUtils.isEmpty(input) && !__.found(PatternsHolder.blank, input);
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
                builder.setTitle(R.string.delete_keyword_indication);
                builder.setMessage(getString(R.string.delete_keyword_warning));

                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((UserKeywordsRecyclerAdapter.ViewHolder) viewHolder).deleteKeyword();
                    }
                });

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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


    public static void start(Context context, String uid) {
        Intent intent = new Intent(context, UserKeywordsActivity.class);
        intent.putExtra(UID, uid);
        context.startActivity(intent);
    }


    private synchronized void unsync() {
        if (keywordsRegistration != null) keywordsRegistration.remove();
    }


    @Override
    public void onStart() {
        super.onStart();
        sync();
    }

    @Override
    public void onStop() {
        super.onStop();
        unsync();
    }
}
