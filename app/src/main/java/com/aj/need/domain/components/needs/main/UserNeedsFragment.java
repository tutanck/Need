package com.aj.need.domain.components.needs.main;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aj.need.R;
import com.aj.need.db.colls.NEEDS;
import com.aj.need.db.colls.PROFILES;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.domain.components.needs.UserNeedAdActivity;
import com.aj.need.domain.components.needs.UserNeedNewSearchActivity;
import com.aj.need.main.A;
import com.aj.need.main.MainActivity;
import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.components.others._Recycler;
import com.aj.need.tools.regina.ack.VoidBAck;
import com.aj.need.tools.utils.Avail;
import com.aj.need.tools.utils.__;
import com.aj.need.tools.regina.ack.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UserNeedsFragment extends Fragment {

    private List<UserNeed> mUserNeeds = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private UserNeedsRecyclerAdapter mAdapter;

    private ProgressBarFragment progressBarFragment;
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

        mRecyclerView = view.findViewById(R.id.needs_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new UserNeedsRecyclerAdapter(getContext(), mUserNeeds);
        mRecyclerView.setAdapter(mAdapter);

        setRecyclerViewItemTouchListener();

        indicationsLayout = view.findViewById(R.id.fragment_user_needs_indications);
        progressBarFragment = (ProgressBarFragment) getChildFragmentManager().findFragmentById(R.id.waiter_modal_fragment);

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
        progressBarFragment.show();
        NEEDS.loadNeeds(A.user_id(getActivity()), new UIAck(getActivity()) {
            @Override
            protected void onRes(Object res, JSONObject ctx) {
                reloadList(res, mUserNeeds, mAdapter, getContext());
            }
        });
    }

    void reloadList(Object res, List<UserNeed> userNeeds, UserNeedsRecyclerAdapter adapter, Context context) {
        try {
            JSONArray jar = (JSONArray) res;
            userNeeds.clear();

            for (int i = 0; i < jar.length(); i++) {
                JSONObject jo = jar.getJSONObject(i);
                userNeeds.add(new UserNeed(
                        jo.getString(Coll._idKey), jo.getString(NEEDS.titleKey)
                        , jo.getString(NEEDS.searchKey), jo.getBoolean(NEEDS.activeKey))
                );
            }

            indicationsLayout.setVisibility(mUserNeeds.size() == 0 ? View.VISIBLE : View.GONE);
            adapter.notifyDataSetChanged();
            progressBarFragment.hide();
        } catch (JSONException e) {
            __.fatal(e); //SNO : if a doc exist the Need field should exist too
        }
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
                        progressBarFragment.show();
                        ((UserNeedsRecyclerAdapter.ViewHolder) viewHolder).deleteNeed(getActivity(), self,
                                A.user_id(getActivity()), mUserNeeds, mAdapter);
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