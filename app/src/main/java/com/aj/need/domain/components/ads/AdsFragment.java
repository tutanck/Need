package com.aj.need.domain.components.ads;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.colls.USER_NEEDS;
import com.aj.need.domain.components.needs.userneeds.UserNeed;
import com.aj.need.tools.utils.Jarvis;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class AdsFragment extends Fragment {

    private List<UserNeed> adList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private AdsRecyclerAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout indicationsLayout;


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_recycler_view_with_fab, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new AdsRecyclerAdapter(getContext(), adList);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = view.findViewById(R.id.recycler_view_SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        indicationsLayout = view.findViewById(R.id.component_recycler_indications_layout);

        TextView indicationTV1 = view.findViewById(R.id.indicationTV1);
        indicationTV1.setText("Aucune annonce autour de vous en ce moment.");

        TextView indicationTV2 = view.findViewById(R.id.indicationTV2);
        indicationTV2.setText("Revenez un peu plus tard.");

        FloatingActionButton fab = view.findViewById(R.id.fab_recycler_action);

        fab.setBackgroundTintList(ColorStateList.valueOf
                (ContextCompat.getColor(getContext(), R.color.Transparent))
        );
        fab.setImageResource(R.drawable.ic_save_gold_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           //todo new act with  this frag as core
            }
        });
        fab.setVisibility(View.GONE);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        load();
    }


    private void load() {
        mSwipeRefreshLayout.setRefreshing(true);
        USER_NEEDS.getCurrentUserNeedsRef()
                .whereEqualTo(USER_NEEDS.deletedKey, false)
                .orderBy(USER_NEEDS.activeKey, Query.Direction.DESCENDING).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        adList.clear();
                        adList.addAll(new Jarvis<UserNeed>().tr(querySnapshot, new UserNeed()));
                        indicationsLayout.setVisibility(adList.size() == 0 ? View.VISIBLE : View.GONE);
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.scrollToPosition(0);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }


    public static AdsFragment newInstance() {
        return new AdsFragment();
    }
}