package com.aj.need.tools.components.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.colls.USERS;
import com.aj.need.tools.components.services.ComponentsServices;
import com.aj.need.tools.components.services.Ic;
import com.aj.need.tools.utils.KeyboardServices;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


public class IDKeyFormField extends Fragment {

    private static final String FORM_FIELD_ID = "FORM_FIELD_ID";
    private static final String _ID = "_ID";
    private static final String LAYOUT_ID = "LAYOUT_ID";
    private static final String KEY = "KEY";
    private static final String LABEL = "LABEL";
    private static final String EDITABLE = "EDITABLE";

    private boolean isOpen = false;

    private RelativeLayout formFieldLayout;
    private ImageView ivIndication;
    private TextView tvContent;
    private EditText etContent;
    private TextInputLayout textInputLayout;
    private TextView tvDescription;
    private View divider;

    private String key;

    private int formFieldId;

    private String _id;

    private boolean editable;


    //instance parameters

    public static IDKeyFormField newInstance(
            int formFieldId
            , String _id
            , String label
            , String key
            , int layoutID
            , boolean editable
    ) {
        IDKeyFormField fragment = new IDKeyFormField();

        Bundle args = new Bundle();
        args.putInt(FORM_FIELD_ID, formFieldId);
        args.putBoolean(EDITABLE, editable);
        args.putString(KEY, key);
        args.putString(_ID, _id);
        args.putInt(LAYOUT_ID, layoutID);
        args.putString(LABEL, label);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        super.onCreateView(inflater, container, savedInstanceState);

        final Bundle args = getArguments();

        formFieldId = args.getInt(FORM_FIELD_ID);

        key = args.getString(KEY);

        _id = args.getString(_ID);

        editable = args.getBoolean(EDITABLE);

        View view = inflater.inflate(args.getInt(LAYOUT_ID), container, false);

        divider = view.findViewById(R.id.divider);

        formFieldLayout = view.findViewById(R.id.form_field_layout);

        ivIndication = view.findViewById(R.id.ivIndication);
        ivIndication.setImageResource(Ic.icon(key));

        textInputLayout = view.findViewById(R.id.text_input_layout);
        textInputLayout.setHint(args.getString(LABEL));

        tvDescription = view.findViewById(R.id.tvDescription);
        tvDescription.setText(args.getString(LABEL));

        etContent = view.findViewById(R.id.etContent);
        etContent.setText("");
        etContent.setVisibility(View.GONE);


        tvContent = view.findViewById(R.id.tvContent);
        tvContent.setText("");

        if (editable)
            ComponentsServices.setSelectable(
                    getActivity(), getLayout(), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (!isOpen()) open();
                            else
                                USERS.getCurrentUserRef().update(getKey(), getETText())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                close();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                __.showShortToast(getContext(), "Echec de la mise à jour");
                                            }
                                        });
                        }
                    }
            );

        return view;
    }


    public void open() {
        if (isOpen) return;
        etContent.setText(tvContent.getText());
        etContent.setVisibility(View.VISIBLE);
        tvContent.setVisibility(View.GONE);
        tvDescription.setVisibility(View.GONE);
        ivIndication.setImageResource(R.drawable.ic_done_24dp);
        divider.setVisibility(View.GONE);
        isOpen = true;
    }


    public void close() {
        if (!isOpen) return;
        tvContent.setText(etContent.getText());
        etContent.setVisibility(View.GONE);
        tvContent.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.VISIBLE);
        ivIndication.setImageResource(Ic.icon(key));
        divider.setVisibility(View.VISIBLE);
        isOpen = false;
        KeyboardServices.dismiss(getContext(), etContent);
    }


    public RelativeLayout getLayout() {
        return formFieldLayout;
    }

    public ImageView getIvIndication() {
        return ivIndication;
    }

    public TextView getTvContent() {
        return tvContent;
    }

    public EditText getEtContent() {
        return etContent;
    }

    public TextInputLayout getTextInputLayout() {
        return textInputLayout;
    }

    public TextView getTvDescription() {
        return tvDescription;
    }

    public String getKey() {
        return key;
    }

    public boolean isOpen() {
        return isOpen;
    }

    private String getETText() {
        return getEtContent().getText().toString().trim();
    }
}