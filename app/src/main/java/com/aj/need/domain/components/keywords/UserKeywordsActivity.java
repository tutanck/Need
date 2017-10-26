package com.aj.need.domain.components.keywords;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.aj.need.db.IO;
import com.aj.need.db.colls.USER_KEYWORDS;
import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.utils.Jarvis;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserKeywordsActivity extends AppCompatActivity {

    private final static String UID = "UID";

    private String uid = null;

    private List<UserKeyword> mUserKeywords = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private UserKeywordsRecyclerAdapter mAdapter;

    private EditText etKeyword;
    private ImageButton btnAdd;

    private LinearLayout indicationsLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_keywords);

        uid = getIntent().getStringExtra(UID);

        if (uid == null) __.fatal("UserKeywordsActivity : uid == null");

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new UserKeywordsRecyclerAdapter(UserKeywordsActivity.this, mUserKeywords, IO.isCurrentUser(uid));
        mRecyclerView.setAdapter(mAdapter);

        indicationsLayout = findViewById(R.id.component_recycler_indications_layout); //// TODO: 22/10/2017 uther indic
        etKeyword = findViewById(R.id.add_keyword_input);

        btnAdd = findViewById(R.id.add_keyword_button);
        btnAdd.setEnabled(false);

        if (IO.isCurrentUser(uid)) {
            functionalizeETKeyword();
            functionalizeBtnAdd();
            setRecyclerViewItemTouchListener();
        } else {
            btnAdd.setVisibility(View.GONE);
            etKeyword.setVisibility(View.GONE);
        }

    }


    public static void start(Context context, String uid) {
        Intent intent = new Intent(context, UserKeywordsActivity.class);
        intent.putExtra(UID, uid);
        context.startActivity(intent);
    }


    @Override
    protected void onStart() {
        super.onStart();
        loadKeywords();
    }


    private void loadKeywords() {

        USER_KEYWORDS.getUserKeywordsRef(getIntent().getStringExtra(UID))
                .whereEqualTo(USER_KEYWORDS.deletedKey, false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mUserKeywords.clear();
                            mUserKeywords.addAll(new Jarvis<UserKeyword>().tr(task.getResult(), new UserKeyword()));
                            indicationsLayout.setVisibility(mUserKeywords.size() == 0 ? View.VISIBLE : View.GONE);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            __.showShortToast(UserKeywordsActivity.this, "Impossible de charger les mots clés");
                            //// TODO: 14/10/2017  reload btn
                        }
                    }
                });
    }


    void saveKeyword(String keyword, boolean active, boolean deleted) {
        if (isKeyword(keyword)) {
            USER_KEYWORDS.getCurrentUserKeywordsRef()
                    .document(keyword).set(new UserKeyword(keyword, active, deleted))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            etKeyword.setText("");
                            loadKeywords();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //// TODO: 26/10/2017
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
