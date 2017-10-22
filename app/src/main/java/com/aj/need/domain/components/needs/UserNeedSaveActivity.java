package com.aj.need.domain.components.needs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;

import com.aj.need.R;
import com.aj.need.db.colls.USER_NEEDS;

import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.components.fragments.FormField;
import com.aj.need.tools.components.services.ComponentsServices;
import com.aj.need.tools.components.services.FormFieldKindTranslator;

import com.aj.need.tools.utils.JSONServices;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class UserNeedSaveActivity extends AppCompatActivity implements FormField.Listener {

    ProgressBarFragment progressBarFragment;

    private final static String _ID = "_ID";
    private final static String SEARCH_TEXT = "SEARCH_TEXT";

    private String _id = null;

    private boolean isFormOpen = false;

    private Map<String, FormField> formFields = new HashMap<>();

    private Switch needSwitch;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_need_save);

        _id = getIntent().getStringExtra(_ID);

        progressBarFragment = (ProgressBarFragment) getSupportFragmentManager().findFragmentById(R.id.waiter_modal_fragment);

        //no duplicated fragments // TODO: 25/09/2017  check if frag only or else like listener on needSwitch
        if (savedInstanceState == null) try {
            JSONObject formParams = JSONServices.loadJsonFromAsset("form_params_user_need.json", this);
            JSONArray orderedFieldsKeys = formParams.getJSONArray("ordered_fields_names");

            for (int i = 0; i < orderedFieldsKeys.length(); i++) {
                String key = orderedFieldsKeys.getString(i);

                JSONObject fieldParam = formParams.getJSONObject(key);

                FormField formField = FormField.newInstance(i,
                        fieldParam.getString("label"), key, FormFieldKindTranslator.tr(fieldParam.getInt("kind")));

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.need_form_layout, formField, key)
                        .commit();

                formFields.put(key, formField);
            }

        } catch (JSONException e) {
            __.fatal(e);
        }


        needSwitch =  findViewById(R.id.need_switch);
        needSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setEnabled(true);
                fab.setVisibility(View.VISIBLE);
            }
        });


        fab =  findViewById(R.id.fab_save_need);
        fab.setEnabled(false);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validState()) {
                    progressBarFragment.show();
                    fab.setEnabled(false); //update in progress


                    Map<String, Object> need = new HashMap<>();

                    need.put(USER_NEEDS.activeKey, needSwitch.isChecked());
                    need.put(USER_NEEDS.deletedKey, false);

                    for (String key : formFields.keySet())
                        if (key.equals(USER_NEEDS.searchKey))
                            need.put(key, formFields.get(key).getTvContent().getText().toString());
                        else
                            need.put(key, formFields.get(key).getEtContent().getText().toString());


                    OnFailureListener onFailureListener = new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            fab.setEnabled(true);
                            progressBarFragment.hide();
                        }
                    };


                    //it could lead to a bug if upserted docs on update mode (_id = null upsert / new doc iof update)
                    if (_id == null)
                        USER_NEEDS.getCurrentUserNeedsRef().add(need)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        _id = documentReference.getId();

                                        close();
                                        progressBarFragment.hide();
                                        __.showShortToast(UserNeedSaveActivity.this, "Mise à jour réussie !");
                                        //finish(); // TODO: 04/10/2017 uncomment on prod mode
                                    }
                                }).addOnFailureListener(onFailureListener);
                    else
                        USER_NEEDS.getCurrentUserNeedsRef().document(_id).set(need).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                close();
                                progressBarFragment.hide();
                                __.showShortToast(UserNeedSaveActivity.this, "Mise à jour réussie !");
                                //finish(); // TODO: 04/10/2017 uncomment on prod mode
                            }
                        }).addOnFailureListener(onFailureListener);
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (_id == null) {
            needSwitch.setChecked(true);
            formFields.get(USER_NEEDS.searchKey).setText(getIntent().getStringExtra(SEARCH_TEXT));
        } else {
            progressBarFragment.show();
            USER_NEEDS.getCurrentUserNeedsRef().document(_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot need = task.getResult();
                        if (need != null) {
                            Log.d("LOL/UserNeedSaveAct", "DocumentSnapshot data: " + task.getResult().getData());
                            for (String key : formFields.keySet())
                                formFields.get(key).setText(need.getString(key));
                            needSwitch.setChecked(need.getBoolean(USER_NEEDS.activeKey));
                            progressBarFragment.hide();
                        } else {
                            __.fatal("UserNeedSaveActivity::Inconsistent database : need/" + _id + " should exist");
                        }
                    } else {
                        __.showShortToast(UserNeedSaveActivity.this, "Impossible de charger le besoin");
                        //// TODO: 14/10/2017
                    }
                }
            });
        }
    }


    public static void start(Activity activity, String str, boolean update) {
        Intent intent = new Intent(activity, UserNeedSaveActivity.class);
        intent.putExtra(update ? _ID : SEARCH_TEXT, str);
        activity.startActivity(intent);
        if (!update) activity.finish();
    }


    private void open() {
        for (String key : formFields.keySet())
            if (!key.equals(USER_NEEDS.searchKey))
                formFields.get(key).open();
        fab.setEnabled(true);
        fab.setVisibility(View.VISIBLE);
        isFormOpen = true;
    }

    private void close() {
        for (String key : formFields.keySet())
            formFields.get(key).close();
        fab.setEnabled(false);
        fab.setVisibility(View.GONE);
        isFormOpen = false;
    }


    private boolean validState() {
        EditText titleET = formFields.get(USER_NEEDS.titleKey).getEtContent();
        EditText descriptionET = formFields.get(USER_NEEDS.descriptionKey).getEtContent();

        if (TextUtils.isEmpty(titleET.getText())) {
            String errStr = "Le titre doit être renseigné !";
            titleET.setError(errStr);
            __.showShortToast(this, errStr);
            return false;
        }
        titleET.setError(null);

        if (TextUtils.isEmpty(descriptionET.getText())) {
            String errStr = "La description doit être renseignée !";
            descriptionET.setError(errStr);
            __.showShortToast(this, errStr);
            return false;
        }

        descriptionET.setError(null);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (fab.getVisibility() == View.VISIBLE)
            if (!fab.isEnabled()) //updates in progress
                __.showShortSnack(fab, "Des modifications sont en cours !");
            else
                Snackbar.make(fab, "Les modifications seront perdues !", Snackbar.LENGTH_SHORT)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UserNeedSaveActivity.super.onBackPressed();
                            }
                        }).show();
        else super.onBackPressed();
    }


    @Override
    public void onFormFieldCreated(int id, FormField formField) {
        ComponentsServices.setSelectable(
                this, formField.getLayout(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isFormOpen) open();
                    }
                }
        );
    }
}
