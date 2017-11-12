package com.aj.need.domain.components.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.db.colls.USERS;
import com.aj.need.db.colls.USER_RATINGS;
import com.aj.need.domain.components.keywords.UserKeywordsActivity;
import com.aj.need.domain.components.messages.MessagesActivity;
import com.aj.need.tools.components.fragments.FormField;
import com.aj.need.tools.components.fragments.ImageFragment;
import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.components.services.ComponentsServices;
import com.aj.need.tools.components.services.FormFieldKindTranslator;
import com.aj.need.tools.utils.Coord;
import com.aj.need.tools.utils.JSONServices;
import com.aj.need.tools.utils.PatternsHolder;
import com.aj.need.tools.utils._Storage;
import com.aj.need.tools.utils.__;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment implements FormField.Listener.Delegate {

    private static final String EDITABLE = "EDITABLE", UID = "UID", DELEGATE_ID = "DELEGATE_ID";
    private String uid = null;
    private boolean isFormOpen = false, isEditable = false;

    private Map<String, FormField> formFields = new HashMap<>();

    private RadioGroup userTypeRG;
    private RatingBar userRating, ratingControl;

    private int availability, completions = 0;

    private FloatingActionButton fabSaveProfile;

    private ProgressBarFragment progressBarFragment;

    private Coord locationCoord;


    public static ProfileFragment newInstance(
            String user_id, boolean editable, int delegateID
    ) {
        Bundle args = new Bundle();
        args.putBoolean(EDITABLE, editable);
        args.putString(UID, user_id);
        args.putInt(DELEGATE_ID, delegateID);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {

        final Bundle args = getArguments();

        uid = args.getString(UID);

        isEditable = args.getBoolean(EDITABLE);

        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        progressBarFragment = (ProgressBarFragment) getChildFragmentManager().findFragmentById(R.id.waiter_modal_fragment);
        progressBarFragment.setBackgroundColor(Color.TRANSPARENT);

        userRating = view.findViewById(R.id.user_rating);

        ratingControl = view.findViewById(R.id.rating_control);

        if (!isEditable) {
            ratingControl.setIsIndicator(false);
            ratingControl.setOnRatingBarChangeListener(
                    new RatingBar.OnRatingBarChangeListener() {
                        public void onRatingChanged(
                                RatingBar ratingBar
                                , float rating
                                , boolean fromUser
                        ) {
                            if (fromUser)
                                USER_RATINGS.getUserRatingsRef(uid)
                                        .document(IO.getCurrentUserUid())
                                        .set(new UserRating(rating));
                        }
                    }
            );
        } else ratingControl.setVisibility(View.GONE);


        try {
            JSONObject formParams = JSONServices.loadJsonFromAsset("form_params_user_profile.json", getContext());
            JSONArray orderedFieldsKeys = formParams.getJSONArray("ordered_fields_names");

            userTypeRG = view.findViewById(R.id.user_type_radio_group);
            for (int i = 0; i < userTypeRG.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) userTypeRG.getChildAt(i);
                radioButton.setEnabled(isEditable);
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int radioButtonID = userTypeRG.getCheckedRadioButtonId();
                        RadioButton radioButton = userTypeRG.findViewById(radioButtonID);
                        int index = userTypeRG.indexOfChild(radioButton);
                        USERS.getCurrentUserRef().update(USERS.typeKey, index);
                    }
                });
            }

            if (savedInstanceState == null) {//no duplicated fragments // TODO: 25/09/2017  check if frag only or else like listener on needSwitch

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

                fragmentTransaction.add(R.id.profile_image_layout, ImageFragment.newInstance(
                        _Storage.getRef(uid), R.drawable.ic_person_profile_large, isEditable), "profile_image"
                );

                for (int i = 0; i < orderedFieldsKeys.length(); i++) {
                    String key = orderedFieldsKeys.getString(i);
                    JSONObject fieldParam = formParams.getJSONObject(key);

                    FormField formField = FormField.newInstance(key, fieldParam.getString("label")
                            , FormFieldKindTranslator.tr(fieldParam.getInt("kind")), args.getInt(DELEGATE_ID));

                    fragmentTransaction.add(R.id.form_layout, formField, key);
                    formFields.put(key, formField);
                }
                fragmentTransaction.commit();
            }

        } catch (JSONException e) {
            __.fatal(e);
        }


        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserKeywordsActivity.start(getContext(), uid);
            }
        });


        FloatingActionButton fabContact = view.findViewById(R.id.fabContact);
        if (!isEditable)
            fabContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MessagesActivity.start(getContext(), uid
                            , formFields.get(USERS.usernameKey).getTvContent().getText().toString()
                            , availability);
                }
            });
        else fabContact.setVisibility(View.GONE);


        fabSaveProfile = fab = view.findViewById(R.id.fab_save_profile);
        disableSaveBtn();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validState()) {

                    Map<String, Object> updates = new HashMap<>();
                    updates.put(USERS.usernameKey, getFieldText(USERS.usernameKey));
                    updates.put(USERS.resumeKey, getFieldText(USERS.resumeKey));
                    updates.put(USERS.tariffKey, getFieldText(USERS.tariffKey));
                    updates.put(USERS.locationKey, getFieldText(USERS.locationKey));
                    updates.put(USERS.metaLocationCoordKey, locationCoord.toMap());

                    USERS.getCurrentUserRef().update(updates);

                    close();
                    __.showShortToast(getContext(), getString(R.string.update_sucessful_message));
                }
            }
        });


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        completions = 0;

        progressBarFragment.show();

        if (!isEditable)
            USER_RATINGS.getUserRatingsRef(uid).document(IO.getCurrentUserUid()).get()
                    .addOnCompleteListener(getActivity(),
                            new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot ratingDoc = task.getResult();
                                        ratingControl.setRating((ratingDoc != null && ratingDoc.exists()) ? ratingDoc.getLong(USER_RATINGS.ratingKey) : 0);
                                        hideProgressBar();
                                    } else
                                        __.showShortToast(getContext(), "Impossible de charger la note que vous avez attribuée");
                                     /*TODO: 13/10/2017 rem or test if the addiction of getActivity() in addOnCompleteListener is ok and then add to other activities/frag. Error(test with wifi not working)
                                     todo : java.lang.NullPointerException: Attempt to invoke virtual method 'android.content.res.Resources android.content.Context.getResources()' on a null object reference
                                      */
                                }
                            }
                    );


        USERS.getUserRef(uid).get().addOnCompleteListener(getActivity(),
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot profile = task.getResult();
                            if (profile != null && profile.exists()) {
                                Log.d("getUserProfile", "DocumentSnapshot data: " + profile.getData());

                                availability = profile.getLong(USERS.availabilityKey).intValue();

                                //RatingBar:userRating
                                Long userAvgRating = profile.getLong(USER_RATINGS.avgRatingKey);
                                userRating.setRating(userAvgRating == null ? 0 : userAvgRating);

                                //RadioGroup::userTypeRG
                                Long userType = profile.getLong(USERS.typeKey);
                                int selectedIndex = userType == null ? 0 : userType.intValue();
                                ((RadioButton) userTypeRG.getChildAt(selectedIndex)).setChecked(true);

                                //FormField::all
                                for (String key : formFields.keySet())
                                    formFields.get(key).getTvContent().setText(profile.getString(key));

                                locationCoord = Coord.toCoord((Map<String, Double>) profile.get(USERS.metaLocationCoordKey));

                                hideProgressBar();
                            } else __.fatal("ProfileFragment : No such document");
                        } else {
                            Log.d("getUserProfile", "get failed with ", task.getException());
                            __.showShortToast(getContext(), "Impossible de charger le profile"); //// TODO: 13/10/2017
                        }
                    }
                }
        );

    }


    private synchronized void hideProgressBar() {
        final int n = isEditable ? 0 : 1;
        if (completions == n)
            progressBarFragment.hide();
        completions++;
    }


    @Override
    public void onFormFieldCreated(final FormField formField) {
        if (!isEditable) return;

        View.OnClickListener onClickListener;
        switch (formField.getKey()) {
            case USERS.locationKey:
                onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pickPlace();
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

        ComponentsServices.setSelectable(getContext(), formField.getLayout(), onClickListener);
    }




    /*WHERE*/

    public static int PLACE_PICKER_REQUEST = 12;

    private void pickPlace() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            getActivity().startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            apiAvailability.getErrorDialog(getActivity(), e.getConnectionStatusCode(), PLACE_PICKER_REQUEST).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            __.showShortToast(getContext(), getString(R.string.unsupported_operation));
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST)
            if (resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(getContext(), data);

                if (place == null) {
                    __.showShortToast(getContext(), getString(R.string.an_error_occured) + "\n Impossible d'identifier le lieu.");
                    return;
                }

                formFields.get(USERS.locationKey).setText(place.getAddress().toString());
                locationCoord = new Coord(place.getLatLng().latitude, place.getLatLng().longitude);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setCancelable(false);
                builder.setTitle("Localisation");
                builder.setMessage("Cette localisation représente votre position de référence.");

                builder.setPositiveButton(R.string.ok, null);

                builder.setNegativeButton(R.string.update, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pickPlace();
                    }
                });

                builder.show();

                enableSaveBtn();
            }
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
        fabSaveProfile.setEnabled(true);
        fabSaveProfile.setVisibility(View.VISIBLE);
    }

    private void disableSaveBtn() {
        fabSaveProfile.setEnabled(false);
        fabSaveProfile.setVisibility(View.GONE);
    }




     /*FIELDS EDITION/VALIDATION*/

    private List<String> lockedKeys = Arrays.asList(new String[]
            {USERS.usernameKey, USERS.locationKey}
    );


    private boolean isEditableField(String key) {
        return !lockedKeys.contains(key);
    }

    private String getFieldText(String key) {
        FormField ff = formFields.get(key);
        TextView tvORet = isEditableField(key) ? ff.getEtContent() : ff.getTvContent();
        return tvORet.getText().toString();
    }


    private boolean validState() {
        boolean valid = true;

        String username = getFieldText(USERS.usernameKey);
        if (TextUtils.isEmpty(username)) {
            valid = false;
            __.showShortToast(getContext(), getString(R.string.mandatory_username));
        } else if (username.length() < 3) {
            __.showShortToast(getContext(), getString(R.string.minimum_username));
            valid = false;
        } else if (!PatternsHolder.isValidUsername(username)) {
            __.showShortToast(getContext(), getString(R.string.invalid_username));
            valid = false;
        }

        if (TextUtils.isEmpty(getFieldText(USERS.locationKey))) {
            valid = false;
            __.showShortToast(getContext(), getString(R.string.location_must_be_filled));
        }

        return valid;
    }
    
    //// TODO: 12/11/2017 onbackPressed

}