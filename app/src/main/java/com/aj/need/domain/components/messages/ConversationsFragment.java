package com.aj.need.domain.components.messages;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aj.need.R;
import com.aj.need.db.colls.MESSAGES;
import com.aj.need.db.colls.PROFILES;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.domain.components.profile.UserProfilesRecyclerAdapter;
import com.aj.need.main.A;
import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.regina.ack.UIAck;
import com.aj.need.tools.utils.__;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ConversationsFragment extends Fragment {

    private ArrayList<UserProfile> mProfiles = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private UserProfilesRecyclerAdapter mAdapter;

    private LinearLayout indicationsLayout;
    private ProgressBarFragment progressBarFragment;

    public static ConversationsFragment newInstance() {
        return new ConversationsFragment();
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

        mAdapter = new UserProfilesRecyclerAdapter(getContext(), mProfiles);
        mRecyclerView.setAdapter(mAdapter);

        progressBarFragment = (ProgressBarFragment) getChildFragmentManager().findFragmentById(R.id.waiter_modal_fragment);
        progressBarFragment.setBackgroundColor(Color.TRANSPARENT);

        indicationsLayout = view.findViewById(R.id.component_recycler_indications);
        indicationsLayout.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadContacts();
    }


    private void loadContacts() {  //// TODO: 10/10/2017  redo
        progressBarFragment.show();
        MESSAGES.computeUserContacts(A.user_id(getActivity())
                , new UIAck(getActivity()) {
                    @Override
                    protected void onRes(Object res, JSONObject ctx) {
                        progressBarFragment.hide();
                        try {
                            JSONArray jar = (JSONArray) res;
                            mProfiles.clear();
                            int i = 0;
                            for (; i < jar.length(); i++) {
                                JSONObject jo = jar.getJSONObject(i).getJSONObject("profile");
                                mProfiles.add(new UserProfile(
                                        jo.getString(Coll._idKey), jo.getString(PROFILES.authIDKey), jo.getString(PROFILES.usernameKey)
                                        , 0, jo.getInt(PROFILES.availabilityKey)) //// TODO: 05/10/2017 : 0 (reputation)
                                );
                            }
                            indicationsLayout.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
                            mAdapter.notifyDataSetChanged();
                            progressBarFragment.hide();
                        } catch (JSONException e) {
                            __.fatal(e); //SNO : if a doc exist the Need field should exist too
                        }
                    }
                });
    }
}