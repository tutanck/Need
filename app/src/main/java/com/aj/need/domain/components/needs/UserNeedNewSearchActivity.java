package com.aj.need.domain.components.needs;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.db.colls.USERS;
import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.domain.components.profile.UserProfilesRecyclerAdapter;
import com.aj.need.tools.utils.ALGOLIA;
import com.aj.need.tools.utils.Jarvis;
import com.aj.need.tools.utils.__;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UserNeedNewSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static String TAG = "UNeedNewSearchAct";

    //Refresh & indications
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout indicationsLayout;

    // Constants:
    private static final int HITS_PER_PAGE = 20;
    // Number of items before the end of the list past which we start loading more content.
    private static final int LOAD_MORE_THRESHOLD = 5;


    // UI:
    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private UserProfilesRecyclerAdapter mAdapter;
    private List<UserProfile> userProfileList = new ArrayList<>();


    // Search:
    private Client client;
    private Index index;
    private Query query;
    private int lastSearchedSeqNo;
    private int lastDisplayedSeqNo;

    // Pagination:
    private int lastRequestedPage;
    private int lastDisplayedPage;
    private boolean endReached;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_recycler_view_with_fab);

        // Bind UI components.
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(this));
        mAdapter = new UserProfilesRecyclerAdapter(this, userProfileList, 0, Glide.with(this));
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = findViewById(R.id.recycler_view_SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        indicationsLayout = findViewById(R.id.component_recycler_indications_layout);
        TextView indicationTV1 = findViewById(R.id.indicationTV1);
        indicationTV1.setText(R.string.no_search_result);
        TextView indicationTV2 = findViewById(R.id.indicationTV2);
        indicationTV2.setText(R.string.fragment_profiles_search_indication);


        // Init Algolia.
        client = new Client(ALGOLIA.AppID, ALGOLIA.APIKey);
        index = client.getIndex("USERS");

        // Pre-build query.
        query = new Query();
        query.setAttributesToRetrieve("keywords", USERS.availabilityKey, "rating"/*todo repby USERS.avgRatingKey*/, USERS.usernameKey);
        query.setFilters("NOT objectID:" + IO.getCurrentUserUid());
        query.setHitsPerPage(HITS_PER_PAGE);

        FloatingActionButton fab = findViewById(R.id.fab_recycler_action);
        fab.setImageResource(R.drawable.ic_speaker_phone_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = searchView.getQuery().toString();

                if (TextUtils.isEmpty(searchText))
                    __.showShortSnack(view, "Impossible de publier une recherche vide!");
                else
                    UserNeedSaveActivity.start(UserNeedNewSearchActivity.this, searchText, false);
            }
        });

        setRecyclerViewScrollListener();
    }


    // Actions

    private void search() {
        final int currentSearchSeqNo = ++lastSearchedSeqNo;
        lastRequestedPage = 0;
        lastDisplayedPage = -1;
        endReached = false;

        mSwipeRefreshLayout.setRefreshing(true);

        query.setQuery(searchView.getQuery().toString());
        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                if (content != null && error == null) {

                    //Check that the received results are newer that the last displayed results.
                    if (currentSearchSeqNo <= lastDisplayedSeqNo) return;

                    Log.d(TAG, "Algolia results:\n" + content.toString());

                    List<UserProfile> results = new Jarvis<UserProfile>().tr(content.optJSONArray("hits"), new UserProfile());
                    if (results.isEmpty()) {
                        endReached = true;
                    } else {
                        userProfileList.clear();
                        userProfileList.addAll(results);
                        mAdapter.notifyDataSetChanged();
                        lastDisplayedSeqNo = currentSearchSeqNo;
                        lastDisplayedPage = 0;
                    }

                    //Indicate the search's result status
                    indicationsLayout.setVisibility(userProfileList.size() == 0 ? View.VISIBLE : View.GONE);

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

                    List<UserProfile> results = new Jarvis<UserProfile>().tr(content.optJSONArray("hits"), new UserProfile());
                    if (results.isEmpty()) {
                        endReached = true;
                    } else {
                        userProfileList.addAll(results);
                        mAdapter.notifyDataSetChanged();
                        lastDisplayedPage = lastRequestedPage;
                    }
                }else
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


    // SearchView.OnQueryTextListener

    @Override
    public boolean onQueryTextChange(String newText) {
        mSwipeRefreshLayout.setRefreshing(false);

        if (TextUtils.isEmpty(newText)) {
            userProfileList.clear();
            mAdapter.notifyDataSetChanged();
            //Indicate the search's result status
            indicationsLayout.setVisibility(View.GONE);
        } else
            search();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Nothing to do: the search has already been performed by `onQueryTextChange()`.
        // We do try to close the keyboard, though.
        searchView.clearFocus();
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.searchbar_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchMenuItem = menu.findItem(R.id.search);

        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                UserNeedNewSearchActivity.super.onBackPressed();
                return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
            }
        });

        searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName())); //todo : dt work : getSearchableInfo = null
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(this);

        return true;
    }


    public static void start(Context context) {
        context.startActivity(new Intent(context, UserNeedNewSearchActivity.class));
    }

}
