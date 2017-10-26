package com.aj.need.domain.components.needs.userneeds;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.db.colls.USER_NEEDS;
import com.aj.need.domain.components.needs.UserNeedAdActivity;
import com.aj.need.domain.components.needs.UserNeedNewSearchActivity;

import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.components.others._Recycler;
import com.aj.need.tools.utils.Jarvis;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.List;


public class UserNeedsFragment extends Fragment {

    private List<UserNeed> mUserNeeds = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private UserNeedsRecyclerAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout indicationsLayout;

    private UserNeedsFragment self = this;

    public static UserNeedsFragment newInstance() {
        return new UserNeedsFragment();
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_user_needs, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new UserNeedsRecyclerAdapter(getContext(), mUserNeeds);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = view.findViewById(R.id.recycler_view_SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                __.showShortToast(getContext(),"refreshing");
            }
        });

        setRecyclerViewItemTouchListener();

        indicationsLayout = view.findViewById(R.id.component_recycler_indications_layout);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_need);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserNeedNewSearchActivity.start(getContext());
            }
        });

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

        USER_NEEDS.getCurrentUserNeedsRef()
                .whereEqualTo(USER_NEEDS.deletedKey, false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            reloadList(task.getResult());
                        } else {
                            __.showShortToast(getContext(), "impossible de charger les besoins");
                        }

                    }
                });
    }

    void reloadList(QuerySnapshot res) {
        mUserNeeds.clear();

        mUserNeeds.addAll(new Jarvis<UserNeed>().tr(res, new UserNeed()));

        indicationsLayout.setVisibility(mUserNeeds.size() == 0 ? View.VISIBLE : View.GONE);
        mAdapter.notifyDataSetChanged();
    }


    private void setRecyclerViewItemTouchListener() {
        mRecyclerView.addOnItemTouchListener(new _Recycler.ItemTouchListener(
                getContext(), mRecyclerView, new _Recycler.ClickListener() {

            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder, int position) {
                UserNeedAdActivity.start(getContext()
                        , ((UserNeedsRecyclerAdapter.ViewHolder) viewHolder).getUserNeed());
            }

            @Override
            public void onLongClick(final RecyclerView.ViewHolder viewHolder, int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Supprimer le besoin ?");
                builder.setMessage(getString(R.string.delete_need_warning));

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((UserNeedsRecyclerAdapter.ViewHolder) viewHolder).deleteNeed(getActivity(), self,
                                IO.getCurrentUserUid(), mUserNeeds, mAdapter);
                    }
                });

                builder.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
            }
        }));
    }


}