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
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.domain.components.profile.Applicant;
import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.domain.components.profile.UserProfilesRecyclerAdapter;
import com.aj.need.tools.utils.Jarvis;
import com.aj.need.tools.utils.__;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class PokesFragment extends Fragment {

    private final static String TAG = "PokesFrag", NEED_ID = "NEED_ID";

    // Constants:
    /*
    *!important the number of results displayed must always be enough to over-fulfill the screen :
    * The first visible and the last visible items must never be seen on the same screen */
    private final int HITS_PER_PAGE = 10; //// TODO: 27/10/2017  20 in prod

    // Number of items before the end of the list past which we start loading more content.
    private static final int LOAD_MORE_THRESHOLD = 1;//// TODO: 15/11/2017 5 in prod


    // UI:
    private RecyclerView mRecyclerView;
    private UserProfilesRecyclerAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<UserProfile> profileList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout indicationsLayout;


    // Search:
    private Query mLoadQuery;
    private QuerySnapshot lastQuerySnapshot;
    private int lastSyncedSeqNo = 0;


    // Pagination:
    private Boolean pageRequestInProgress = false;


    public static PokesFragment newInstance(String needID) {
        Bundle args = new Bundle();
        args.putString(NEED_ID, needID);
        PokesFragment fragment = new PokesFragment();
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
        mRecyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getActivity()));
        mAdapter = new UserProfilesRecyclerAdapter(getContext(), profileList, 0, Glide.with(this));
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = view.findViewById(R.id.recycler_view_SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });

        indicationsLayout = view.findViewById(R.id.component_recycler_indications_layout);
        TextView indicationTV2 = view.findViewById(R.id.indicationTV2);
        indicationTV2.setText(R.string.fragment_need_profiles_pokes_indication);

        String needID = getArguments().getString(NEED_ID);

        if (needID == null) __.fatal(TAG + ": needID == null !");

        mLoadQuery = APPLICANTS.getAdApplicantsRef(IO.getCurrentUserUid(), needID)
                .orderBy(Coll.dateKey, Query.Direction.DESCENDING);

        setRecyclerViewScrollListener();

        return view;
    }


    private void reload() {
        mSwipeRefreshLayout.setRefreshing(true);

        mLoadQuery.limit(HITS_PER_PAGE).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                Log.d(TAG, "reload: querySnapshot=" + task.getResult() + " error=" + task.getException());

                if (task.isSuccessful()) {
                    // refresh ui
                    refreshList(task.getResult(), true, null);

                    //Indicate the search's result status
                    indicationsLayout.setVisibility(profileList.size() == 0 ? View.VISIBLE : View.GONE);

                    // Scroll the list back to the top.
                    mRecyclerView.scrollToPosition(0);
                } else
                    __.showShortToast(getContext(), getString(R.string.load_error_message));

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private synchronized void refreshList(QuerySnapshot querySnapshot, boolean reset, Integer currentSyncedSeqNo) {
        if (querySnapshot == null) return;
        lastQuerySnapshot = querySnapshot;

        if (reset) {
            lastSyncedSeqNo++;
            profileList.clear();
        } else // Ignore results if they are for an older synced data
            if (currentSyncedSeqNo != lastSyncedSeqNo) return;

        profileList.addAll(new Jarvis<UserProfile>().tr(querySnapshot, new Applicant()));
        mAdapter.notifyDataSetChanged();
        Log.i(TAG, "profileList: " + profileList.toString());
    }


    private void loadMore() {
        final int currentSyncedSeqNo = lastSyncedSeqNo;
        Query loadMoreQuery = mLoadQuery.startAfter(lastQuerySnapshot.getDocuments().get(lastQuerySnapshot.size() - 1));
        loadMoreQuery.limit(HITS_PER_PAGE).get().addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                Log.d(TAG, "loadMore::" + "querySnapshot=" + task.getResult() + " e=", task.getException());

                if (task.isSuccessful())
                    refreshList(task.getResult(), false, currentSyncedSeqNo);
                else
                    __.showShortToast(getContext(), getString(R.string.load_error_message));

                pageRequestInProgress = false;
            }
        });
    }


    private void setRecyclerViewScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();

                // Abort if list is empty or if the initial load does not exists
                if (totalItemCount == 0 || lastQuerySnapshot == null) return;

                // Abort if the end has already been reached (endReached==true).
                if (lastQuerySnapshot.isEmpty()) return;

                // Load more if we are sufficiently close to the end of the list.
                int firstInvisibleItem = linearLayoutManager.findLastVisibleItemPosition() + 1;

                if (firstInvisibleItem + LOAD_MORE_THRESHOLD >= totalItemCount) {
                    synchronized (pageRequestInProgress) {
                        // Ignore if a page request is already in progress
                        if (pageRequestInProgress) return;
                        pageRequestInProgress = true;
                    }
                    loadMore();
                }
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        reload();
    }
}