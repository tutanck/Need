package com.aj.need.domain.components.needs;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.domain.entities.UserProfile;
import com.aj.need.domain.components.profile.UserProfilesRecyclerAdapter;
import com.aj.need.tools.utils.ALGOLIA;
import com.aj.need.tools.utils.Jarvis;
import com.aj.need.tools.utils.__;
import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class UserNeedNewSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    // Constants:
    private static final int HITS_PER_PAGE = 20;
    // Number of items before the end of the list past which we start loading more content.
    private static final int LOAD_MORE_THRESHOLD = 5;


    // UI:
    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private UserProfilesRecyclerAdapter mAdapter;
    private ArrayList<UserProfile> mProfiles = new ArrayList<>();


    // Algolia Search:
    private Client client;
    private Index index;
    private Query query;
    private int lastSearchedSeqNo;
    private int lastDisplayedSeqNo;
    private int lastRequestedPage;
    private int lastDisplayedPage;
    private boolean endReached;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_need_new_search);

        // Bind UI components.
        mRecyclerView = findViewById(R.id.found_profiles_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new UserProfilesRecyclerAdapter(this, mProfiles);
        mRecyclerView.setAdapter(mAdapter);


        // Init Algolia.
        client = new Client(ALGOLIA.AppID, ALGOLIA.APIKey);
        index = client.getIndex("USERS");

        // Pre-build query.
        query = new Query();
        query.setAttributesToRetrieve("keywords", "availability", "rating", "username");
        query.setFilters("NOT objectID:"+ IO.getCurrentUserUid());
        query.setHitsPerPage(HITS_PER_PAGE);

        FloatingActionButton fab = findViewById(R.id.fab_open_need_save);
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

    }


    // Actions

    private void search() {
        final int currentSearchSeqNo = ++lastSearchedSeqNo;
        lastRequestedPage = 0;
        lastDisplayedPage = -1;
        endReached = false;

        query.setQuery(searchView.getQuery().toString());

        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject content, AlgoliaException error) {
                if (content != null && error == null) {
                    // NOTE: Check that the received results are newer that the last displayed results.
                    //
                    // Rationale: Although TCP imposes a server to send responses in the same order as
                    // requests, nothing prevents the system from opening multiple connections to the
                    // same server, nor the Algolia client to transparently switch to another server
                    // between two requests. Therefore the order of responses is not guaranteed.
                    if (currentSearchSeqNo <= lastDisplayedSeqNo) return;

                    Log.d("Algolia results", content.toString());

                    List<UserProfile> results = new Jarvis<UserProfile>().tr(content.optJSONArray("hits"), new UserProfile());
                    if (results.isEmpty())
                        endReached = true;
                    else {
                        mProfiles.clear();
                        mProfiles.addAll(results);
                        mAdapter.notifyDataSetChanged();
                        lastDisplayedSeqNo = currentSearchSeqNo;
                        lastDisplayedPage = 0;
                    }

                    // Scroll the list back to the top.
                    mRecyclerView.smoothScrollToPosition(0);
                } else
                    Log.e("Algolia error", "" + error);
            }
        });
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

        searchView = (SearchView)searchMenuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName())); //todo : dt work : getSearchableInfo = null
        searchView.setIconifiedByDefault(false); //todo dt work
        searchView.setOnQueryTextListener(this);

        return true;
    }


    // SearchView.OnQueryTextListener

    @Override
    public boolean onQueryTextSubmit(String query) {
        // Nothing to do: the search has already been performed by `onQueryTextChange()`.
        // We do try to close the keyboard, though.
        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            mProfiles.clear();
            mAdapter.notifyDataSetChanged();
        }
        else
            search();
        return true;
    }


    public static void start(Context context) {
        context.startActivity(new Intent(context, UserNeedNewSearchActivity.class));
    }

}
