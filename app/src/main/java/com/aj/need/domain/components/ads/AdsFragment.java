package com.aj.need.domain.components.ads;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aj.need.R;
import com.aj.need.tools.utils.__;

import java.util.ArrayList;
import java.util.List;


public class AdsFragment extends Fragment {

    private List<Ad> mAds = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private AdsRecyclerAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static AdsFragment newInstance() {
        return new AdsFragment();
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

        mAdapter = new AdsRecyclerAdapter(getContext(), mAds);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = view.findViewById(R.id.recycler_view_SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                __.showShortToast(getContext(), "refreshing");
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        load();
    }


    private void load() {
    }
}