package com.aj.need.domain.components.needs;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.colls.USER_NEEDS;

import com.aj.need.domain.components.needs.userneeds.UserNeed;
import com.aj.need.tools.components.fragments.DatePickerFragment;
import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.components.fragments.FormField;
import com.aj.need.tools.components.services.ComponentsServices;
import com.aj.need.tools.components.services.FormFieldKindTranslator;

import com.aj.need.tools.utils.Coord;
import com.aj.need.tools.utils.JSONServices;
import com.aj.need.tools.utils.__;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;


public class UserNeedSaveActivity extends AppCompatActivity
        implements FormField.Listener, DatePickerDialog.OnDateSetListener {

    ProgressBarFragment progressBarFragment;

    private final static String _ID = "_ID";
    private final static String SEARCH_TEXT = "SEARCH_TEXT";

    private String _id = null;

    private boolean isFormOpen = false;

    private Map<String, FormField> formFields = new HashMap<>();

    private Switch needSwitch;

    private FloatingActionButton fab;

    private Place place;
    private Calendar cal;



    /*ACTIVITY LIFECYCLE*/

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

                FormField formField = FormField.newInstance(
                        key, fieldParam.getString("label")
                        , FormFieldKindTranslator.tr(fieldParam.getInt("kind")), -9
                );

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.need_form_layout, formField, key)
                        .commit();

                formFields.put(key, formField);
            }

        } catch (JSONException e) {
            __.fatal(e);
        }


        needSwitch = findViewById(R.id.need_switch);
        needSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableSaveBtn();
            }
        });


        fab = findViewById(R.id.fab_save_need);
        disableSaveBtn();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validState()) {
                    final CollectionReference userNeedsRef = USER_NEEDS.getCurrentUserNeedsRef();

                    //!important : it would be a bug if docs were upserted on update mode (_id==null upsert iof update)
                    final DocumentReference curUserNeedRef = (_id != null) ? userNeedsRef.document(_id) : userNeedsRef.document();

                    //!important : avoid creating new needs if initial id==null. Must be set bf creating the UserNeed object
                    _id = curUserNeedRef.getId();

                    curUserNeedRef.set(new UserNeed(_id
                            , getFieldText(USER_NEEDS.searchKey)
                            , getFieldText(USER_NEEDS.titleKey)
                            , getFieldText(USER_NEEDS.descriptionKey)
                            , getFieldText(USER_NEEDS.rewardKey)
                            , getFieldText(USER_NEEDS.whereKey)
                            , getFieldText(USER_NEEDS.whenKey)
                            , getPlace()
                            , getTime()
                            , needSwitch.isChecked())
                    );

                    close();
                    __.showShortToast(UserNeedSaveActivity.this, getString(R.string.update_sucessful_message));
                    //finish(); //TODO: 04/10/2017 uncomment on prod mode
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

        if (_id != null) {
            progressBarFragment.show();
            USER_NEEDS.getCurrentUserNeedsRef().document(_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot need = task.getResult();

                        if (need != null && need.exists()) {
                            Log.d("UserNeedSaveAct/onStart", "getCurrentUserNeeds::need : " + need.getData());

                            for (String key : formFields.keySet())
                                formFields.get(key).setText(need.getString(key));

                            needSwitch.setChecked(need.getBoolean(USER_NEEDS.activeKey));

                            progressBarFragment.hide();
                        } else {
                            Log.e("UserNeedSaveAct/onStart", "UserNeedSaveActivity::Inconsistent database : need/" + _id + " should exist");
                            __.apologize(UserNeedSaveActivity.this, true);
                        }

                    } else {
                        Log.e("UserNeedSaveAct/onStart", "Failed to load need/" + _id, task.getException());
                        __.apologize(UserNeedSaveActivity.this, true);
                    }
                }
            });
        } else {
            needSwitch.setChecked(true);
            formFields.get(USER_NEEDS.searchKey).setText(getIntent().getStringExtra(SEARCH_TEXT));
        }
    }


    @Override
    public void onFormFieldCreated(FormField formField) {
        View.OnClickListener onClickListener;

        switch (formField.getKey()) {
            case USER_NEEDS.whereKey:
                onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pickPlace();
                    }
                };
                break;
            case USER_NEEDS.whenKey:
                onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pickDate();
                    }
                };
                break;
            default:
                onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        open();
                    }
                };
                break;
        }

        ComponentsServices.setSelectable(this, formField.getLayout(), onClickListener);
    }




    /*WHERE*/

    private int PLACE_PICKER_REQUEST = 12;

    private void pickPlace() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            apiAvailability.getErrorDialog(this, e.getConnectionStatusCode(), PLACE_PICKER_REQUEST).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            __.showShortToast(this, getString(R.string.unsupported_operation));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST)
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);
                formFields.get(USER_NEEDS.whereKey).setText(place.getAddress().toString());
                enableSaveBtn();
            }
    }

    private Coord getPlace() {
        return place == null ? null : new Coord(place.getLatLng());
    }



    /*WHEN*/

    private void pickDate() {
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.show(getSupportFragmentManager(), "Date dialog");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        cal = new GregorianCalendar(year, month, day);
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
        formFields.get(USER_NEEDS.whenKey).setText(dateFormat.format(cal.getTime()));
        enableSaveBtn();
    }

    private Long getTime() {
        return cal == null ? null : cal.getTimeInMillis();
    }




    /*FORM TOGGLE*/

    private void open() {
        if (isFormOpen) return;
        for (String key : formFields.keySet())
            if (isEditableField(key))
                formFields.get(key).open();
        enableSaveBtn();
        isFormOpen = true;
    }

    private void close() {
        disableSaveBtn();//!important : should be the 1st instruction cause of uneditable fields (where/when)
        if (!isFormOpen) return;
        for (String key : formFields.keySet())
            if (isEditableField(key))
                formFields.get(key).close();
        isFormOpen = false;
    }


    private void enableSaveBtn() {
        fab.setEnabled(true);
        fab.setVisibility(View.VISIBLE);
    }

    private void disableSaveBtn() {
        fab.setEnabled(false);
        fab.setVisibility(View.GONE);
    }




     /*FIELDS EDITION/VALIDATION*/

    private boolean isEditableField(String key) {
        return !(
                key.equals(USER_NEEDS.searchKey)
                        || key.equals(USER_NEEDS.whereKey)
                        || key.equals(USER_NEEDS.whenKey)
        );
    }

    private String getFieldText(String key) {
        FormField ff = formFields.get(key);
        TextView tvORet = isEditableField(key) ? ff.getEtContent() : ff.getTvContent();
        return tvORet.getText().toString();
    }


    private boolean validState() {
        EditText titleET = formFields.get(USER_NEEDS.titleKey).getEtContent();
        EditText descriptionET = formFields.get(USER_NEEDS.descriptionKey).getEtContent();

        if (TextUtils.isEmpty(titleET.getText())) {
            String errStr = getString(R.string.user_need_empty_title_warning);
            titleET.setError(errStr);
            __.showShortToast(this, errStr);
            return false;
        }
        titleET.setError(null);

        if (TextUtils.isEmpty(descriptionET.getText())) {
            String errStr = getString(R.string.user_need_empty_description_warning);
            descriptionET.setError(errStr);
            __.showShortToast(this, errStr);
            return false;
        }
        descriptionET.setError(null);

        return true;
    }




    /*ACTIVITY BOOT/EXIT*/

    public static void start(Activity activity, String str, boolean update) {
        Intent intent = new Intent(activity, UserNeedSaveActivity.class);
        intent.putExtra(update ? _ID : SEARCH_TEXT, str);
        activity.startActivity(intent);
        activity.finish();
    }


    @Override
    public void onBackPressed() {
        if (fab.getVisibility() == View.VISIBLE)
            Snackbar.make(fab, R.string.user_need_changes_will_be_lost_message, Snackbar.LENGTH_SHORT)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UserNeedSaveActivity.super.onBackPressed();
                        }
                    }).show();
        else super.onBackPressed();
    }

}
