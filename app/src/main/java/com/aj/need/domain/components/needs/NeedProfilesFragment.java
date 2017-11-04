package com.aj.need.domain.components.needs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.db.colls.APPLICANTS;
import com.aj.need.domain.components.profile.Applicant;
import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.domain.components.profile.UserProfilesRecyclerAdapter;
import com.aj.need.tools.utils.Jarvis;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class NeedProfilesFragment extends Fragment {

    private final static String POS = "POS", NEED = "NEED";


    private ArrayList<UserProfile> profileList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private UserProfilesRecyclerAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout indicationsLayout;

    private Query mLoadQuery;
    private QuerySnapshot lastQuerySnapshot;


    public static NeedProfilesFragment newInstance(int pos, UserNeed userNeed) {
        Bundle args = new Bundle();
        args.putInt(POS, pos);
        args.putSerializable(NEED,userNeed);
        NeedProfilesFragment fragment = new NeedProfilesFragment();
        fragment.setArguments(args);
        return fragment;
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

        mAdapter = new UserProfilesRecyclerAdapter(getContext(), profileList, 0);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = view.findViewById(R.id.recycler_view_SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (lastQuerySnapshot == null) loadProfiles();
                else mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        indicationsLayout = view.findViewById(R.id.component_recycler_indications_layout);
        TextView indicationTV1 = view.findViewById(R.id.indicationTV1);
        TextView indicationTV2 = view.findViewById(R.id.indicationTV2);

        indicationTV2.setText(getArguments().getInt(POS) == 1 ?
                R.string.fragment_need_profiles_indic1 :
                R.string.fragment_need_profiles_indic2);

        UserNeed userNeed = ((UserNeed) getArguments().getSerializable(NEED));

        mLoadQuery = APPLICANTS.getAdApplicantsRef(IO.getCurrentUserUid(),userNeed.get_id() );
        //// TODO: 28/10/2017  limit and orderBy date and vu !important

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        loadProfiles();
    }


    //// TODO: 28/10/2017 loadMore (offset)
    private synchronized /*!important : sync access to shared attributes (isLoading, etc)*/
    void loadProfiles() {  //// TODO: 10/10/2017  redo
        mSwipeRefreshLayout.setRefreshing(true); //useful only for loadMore
        mLoadQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //!important : useful log for index issues tracking, etc.
                Log.d("NeedProfilesFragment", "onStart/onComplete::querySnapshot=" + task.getResult() + " error=" + task.getException());
                if (task.isSuccessful())
                    refreshProfileList(task.getResult(), true);
                else
                    __.showShortToast(getContext(), getString(R.string.load_error_message));

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private synchronized void refreshProfileList(QuerySnapshot querySnapshot, boolean reset) {
        if (querySnapshot == null) return;
        lastQuerySnapshot = querySnapshot;
        if (reset) profileList.clear();

        profileList.addAll(new Jarvis<UserProfile>().tr(querySnapshot, new Applicant()));
        Log.i("profileList", profileList.toString());
        indicationsLayout.setVisibility(profileList.size() == 0 ? View.VISIBLE : View.GONE);
        mAdapter.notifyDataSetChanged();
        if (reset) mRecyclerView.scrollToPosition(0);
    }

}