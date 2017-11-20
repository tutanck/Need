package com.aj.need.domain.components.ads;

import android.os.Bundle;
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
import com.aj.need.db.colls.USER_NEEDS;
import com.aj.need.domain.components.needs.UserNeed;
import com.aj.need.tools.utils.ALGOLIA;
import com.aj.need.tools.utils.Jarvis;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.bumptech.glide.Glide;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class AdsFragment extends Fragment {

    private final static String TAG = "AdsFrag", QUERY_STRING = "QUERY_STRING";


    //Refresh & indications
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout indicationsLayout;


    // Constants:
    private static final int HITS_PER_PAGE = 20;
    // Number of items before the end of the list past which we start loading more content.
    private static final int LOAD_MORE_THRESHOLD = 5;


    // UI:
    private RecyclerView mRecyclerView;
    private AdsRecyclerAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<UserNeed> adList = new ArrayList<>();


    // Search:
    private Client client;
    private Index index;
    private com.algolia.search.saas.Query query;
    private int lastSearchedSeqNo;
    private int lastDisplayedSeqNo;


    // Pagination:
    private int lastRequestedPage;
    private int lastDisplayedPage;
    private boolean endReached;


    // Fragment instantiation
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

        // Bind UI components.
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getActivity()));
        mAdapter = new AdsRecyclerAdapter(getContext(), adList, Glide.with(this));
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = view.findViewById(R.id.recycler_view_SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                search();
            }
        });

        indicationsLayout = view.findViewById(R.id.component_recycler_indications_layout);
        TextView indicationTV2 = view.findViewById(R.id.indicationTV2);
        indicationTV2.setText(R.string.no_ads_for_now);


        // Init Algolia.
        client = new Client(ALGOLIA.AppID, ALGOLIA.APIKey);
        index = client.getIndex("NEEDS");

        // Pre-build query.
        query = new Query();
        query.setAttributesToRetrieve(
                USER_NEEDS.ownerIDKey
                , USER_NEEDS.ownerNameKey

                , USER_NEEDS.searchKey
                , USER_NEEDS.titleKey
                , USER_NEEDS.descriptionKey
                , USER_NEEDS.rewardKey
                , USER_NEEDS.whereKey

                , USER_NEEDS.metaIsWhereVisibleKey
                , USER_NEEDS.metaWhereCoordKey

                , USER_NEEDS.deletedKey
                , USER_NEEDS.activeKey
                , USER_NEEDS.dateKey
        );
        query.setFilters("NOT " + USER_NEEDS.ownerIDKey + ":" + IO.getCurrentUserUid());
        query.setHitsPerPage(HITS_PER_PAGE);

        setRecyclerViewScrollListener();

        return view;
    }


    // Actions

    private void search() {
        final int currentSearchSeqNo = ++lastSearchedSeqNo;
        lastRequestedPage = 0;
        lastDisplayedPage = -1;
        endReached = false;

        mSwipeRefreshLayout.setRefreshing(true);

        String queryString = "";
        query.setQuery(queryString);
        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                if (content != null && error == null) {

                    //Check that the received results are newer that the last displayed results.
                    if (currentSearchSeqNo <= lastDisplayedSeqNo) return;

                    Log.d(TAG, "Algolia results:\n" + content.toString());

                    List<UserNeed> results = new Jarvis<UserNeed>().tr(content.optJSONArray("hits"), new UserNeed());
                    if (results.isEmpty()) {
                        endReached = true;
                    } else {
                        adList.clear();
                        adList.addAll(results);
                        mAdapter.notifyDataSetChanged();
                        lastDisplayedSeqNo = currentSearchSeqNo;
                        lastDisplayedPage = 0;
                    }

                    //Indicate the search's result status
                    indicationsLayout.setVisibility(adList.size() == 0 ? View.VISIBLE : View.GONE);

                    // Scroll the list back to the top.
                    mRecyclerView.smoothScrollToPosition(0);
                } else
                    Log.e(TAG, "Algolia error", error);

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private void loadMore() {
        Query loadMoreQuery = new Query(query);
        loadMoreQuery.setPage(++lastRequestedPage);
        final int currentSearchSeqNo = lastSearchedSeqNo;
        index.searchAsync(loadMoreQuery, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                if (content != null && error == null) {

                    // Ignore results if they are for an older query.
                    if (lastDisplayedSeqNo != currentSearchSeqNo) return;

                    Log.d(TAG, "Algolia results:\n" + content.toString());

                    List<UserNeed> results = new Jarvis<UserNeed>().tr(content.optJSONArray("hits"), new UserNeed());
                    if (results.isEmpty()) {
                        endReached = true;
                    } else {
                        adList.addAll(results);
                        mAdapter.notifyDataSetChanged();
                        lastDisplayedPage = lastRequestedPage;
                    }
                } else
                    Log.e(TAG, "Algolia error", error);
            }
        });
    }


    private void setRecyclerViewScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();

                // Abort if list is empty or the end has already been reached.
                if (totalItemCount == 0 || endReached) return;

                // Ignore if a new page has already been requested.
                if (lastRequestedPage > lastDisplayedPage) return;

                // Load more if we are sufficiently close to the end of the list.
                int firstInvisibleItem = linearLayoutManager.findLastVisibleItemPosition() + 1;
                if (firstInvisibleItem + LOAD_MORE_THRESHOLD >= totalItemCount)
                    loadMore();
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        search();
    }
}