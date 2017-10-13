package com.aj.need.tools.components.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.aj.need.R;


public class ProgressBarFragment extends Fragment {

    private static final String VISIBLE = "VISIBLE";

    private RelativeLayout progressBarLayout;

    public static ProgressBarFragment newInstance(
    ) {
        return new ProgressBarFragment();
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        progressBarLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_progressbar, container, false);
        progressBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        return progressBarLayout;
    }


    public void show() {
        progressBarLayout.setVisibility(View.VISIBLE);
    }

    public void hide() {
        progressBarLayout.setVisibility(View.GONE);
    }

    public void setBackgroundColor(int color) {
        progressBarLayout.setBackgroundColor(color);
    }

}