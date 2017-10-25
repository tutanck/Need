package com.aj.need.domain.components.needs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aj.need.R;
import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.domain.components.profile.UserProfilesRecyclerAdapter;

import java.util.ArrayList;


public class NeedProfilesFragment extends Fragment {

    private ArrayList<UserProfile> mProfiles = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private UserProfilesRecyclerAdapter mAdapter;


    public static NeedProfilesFragment newInstance() {
        return new NeedProfilesFragment();
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.component_recycler_view, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new UserProfilesRecyclerAdapter(getContext(), mProfiles, 0);
        mRecyclerView.setAdapter(mAdapter);

        return view;
    }

}