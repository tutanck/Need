package com.aj.need.domain.components.profile;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.db.colls.USERS;
import com.aj.need.db.colls.USER_RATINGS;
import com.aj.need.domain.components.keywords.UserKeywordsActivity;
import com.aj.need.domain.components.messages.MessagesActivity;
import com.aj.need.tools.components.fragments.IDKeyFormField;
import com.aj.need.tools.components.fragments.ImageFragment;
import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.components.services.FormFieldKindTranslator;
import com.aj.need.tools.utils.JSONServices;
import com.aj.need.tools.utils._Storage;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {

    private static final String EDITABLE = "EDITABLE";

    private final static String UID = "UID";

    private String uid = null;

    private boolean isEditable = false;

    private JSONObject formParams;

    private ProgressBarFragment progressBarFragment;

    private Map<String, IDKeyFormField> formFields = new HashMap<>();

    private RadioGroup userTypeRG;

    private  RatingBar userRating;

    private  RatingBar ratingControl;


    public static ProfileFragment newInstance(
            String user_id,
            boolean editable
    ) {
        Bundle args = new Bundle();
        args.putBoolean(EDITABLE, editable);
        args.putString(UID, user_id);
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
            USER_RATINGS.getCurrentUserRatingsRef().document(uid).get()
                    .addOnCompleteListener(
                            new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot ratingDoc = task.getResult(); //// TODO: 23/10/2017  dt work
                                        ratingControl.setRating((ratingDoc != null && ratingDoc.exists()) ? ratingDoc.getLong(USER_RATINGS.ratingKey) : 0);
                                    } else
                                        __.showShortToast(getContext(), "impossible de charger la note attribuée"); //// TODO: 13/10/2017

                                }
                            }
                    );

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
            formParams = JSONServices.loadJsonFromAsset("form_params_user_profile.json", getContext());
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

                fragmentTransaction.add(R.id.profile_image_layout, ImageFragment.newInstance( //todo bug : no view found for "profile_image"
                        _Storage.getRef(uid), R.drawable.ic_person_profile_large, isEditable), "profile_image"
                );

                for (int i = 0; i < orderedFieldsKeys.length(); i++) {
                    String key = orderedFieldsKeys.getString(i);
                    JSONObject fieldParam = formParams.getJSONObject(key);

                    IDKeyFormField formField = IDKeyFormField.newInstance
                            (i, uid, fieldParam.getString("label"), key
                                    , FormFieldKindTranslator.tr(fieldParam.getInt("kind")), isEditable);

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
                            , formFields.get(USERS.usernameKey).getTvContent().getText().toString(), null); //// TODO: 15/10/2017  make a request to know if there was a conv between two interlocutors and if yes get the conversationID
                }
            });
        else fabContact.setVisibility(View.GONE);


        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        progressBarFragment.show();

        USERS.getUserRef(uid).get().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot profile = task.getResult();
                            if (profile != null && profile.exists()) {
                                Log.d("getUserProfile", "DocumentSnapshot data: " + profile.getData());

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

                                progressBarFragment.hide();
                            } else __.fatal("ProfileFragment : No such document");
                        } else {
                            Log.d("getUserProfile", "get failed with ", task.getException());
                            __.showShortToast(getContext(), "Impossible de charger le profile"); //// TODO: 13/10/2017
                        }

                    }
                }
        );

    }
}