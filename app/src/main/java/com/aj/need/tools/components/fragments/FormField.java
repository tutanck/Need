package com.aj.need.tools.components.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.aj.need.tools.components.services.Ic;
import com.aj.need.tools.utils.KeyboardServices;


public class FormField extends Fragment {

    private static final String KEY = "KEY";
    private static final String LABEL = "LABEL";
    private static final String LAYOUT_ID = "LAYOUT_ID";
    private static final String DELEGATE_ID = "DELEGATE_ID";

    private boolean isOpen = false;

    private RelativeLayout formFieldLayout;
    private ImageView ivIndication;
    private TextView tvContent;
    private EditText etContent;
    private TextInputLayout textInputLayout;
    private TextView tvDescription;
    private View divider;

    private Listener mListener;

    private String key;
    private int delegateID;


    //instance parameters

    public static FormField newInstance(
            String key, String label, int layoutID, int delegateID
    ) {
        FormField fragment = new FormField();

        Bundle args = new Bundle();
        args.putString(KEY, key);
        args.putString(LABEL, label);
        args.putInt(LAYOUT_ID, layoutID);
        args.putInt(DELEGATE_ID, delegateID);
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

        key = args.getString(KEY);
        delegateID = args.getInt(DELEGATE_ID);

        View view = inflater.inflate(args.getInt(LAYOUT_ID), container, false);

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

        divider = view.findViewById(R.id.divider);
        divider.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener.onFormFieldCreated(this);
    }

    public void open() {
        //!important : if the field is already open, reopen it could override the etContent's text
        if (isOpen) return;
        etContent.setText(tvContent.getText());
        etContent.setVisibility(View.VISIBLE);
        tvContent.setVisibility(View.GONE);
        tvDescription.setVisibility(View.GONE);
        isOpen = true;
    }


    public void close() {
        //!important : if the field is already closed, close it could override the tvContent's text
        if (!isOpen) return;
        tvContent.setText(etContent.getText());
        etContent.setVisibility(View.GONE);
        tvContent.setVisibility(View.VISIBLE);
        tvDescription.setVisibility(View.VISIBLE);
        isOpen = false;
        KeyboardServices.dismiss(getContext(), etContent);
    }


    public void setText(String text) {
        getEtContent().setText(text);
        getTvContent().setText(text);
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

    public int getDelegateID() {
        return delegateID;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public View getDivider() {
        return divider;
    }


    public interface Listener {
        void onFormFieldCreated(FormField formField);

        interface Delegate {
            void onFormFieldCreated(FormField formField);
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) mListener = (Listener) context;
        else throw new RuntimeException(context.toString() + " must implement FormField.Listener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}