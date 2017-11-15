package com.aj.need.domain.components.messages;

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
import com.aj.need.db.colls.USER_CONTACTS;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.domain.components.profile.Contact;
import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.domain.components.profile.UserProfilesRecyclerAdapter;
import com.aj.need.tools.utils.Jarvis;
import com.aj.need.tools.utils.__;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ConversationsFragment extends Fragment {

    private final static String TAG = "ConversationsFrag";

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
    private List<UserProfile> contactList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout indicationsLayout;


    // Search:
    private Query mLoadQuery;
    private QuerySnapshot lastQuerySnapshot;

    private ListenerRegistration contactsRegistration;


    // Pagination:
    private int lastRequestedPage;
    private int lastDisplayedPage;
    private boolean endReached;


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
        mRecyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getActivity()));
        mAdapter = new UserProfilesRecyclerAdapter(getContext(), contactList, 1, Glide.with(this));
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = view.findViewById(R.id.recycler_view_SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                sync();
            }
        });

        indicationsLayout = view.findViewById(R.id.component_recycler_indications_layout);
        TextView indicationTV1 = view.findViewById(R.id.indicationTV1);
        TextView indicationTV2 = view.findViewById(R.id.indicationTV2);

        indicationTV1.setText(R.string.fragment_conversation_indic1);
        indicationTV2.setText(R.string.fragment_conversation_indic2);

        mLoadQuery = USER_CONTACTS.getCurrentUserContactsRef()
                .orderBy(Coll.dateKey, Query.Direction.DESCENDING);

        setRecyclerViewScrollListener();

        return view;
    }


    private synchronized void sync() {
        lastRequestedPage = 0;
        lastDisplayedPage = -1;
        endReached = false;

        mSwipeRefreshLayout.setRefreshing(true);

        unsync();
        contactsRegistration = mLoadQuery.limit(HITS_PER_PAGE).addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot querySnapshot, FirebaseFirestoreException e) {
                Log.i(TAG, "contactsRegistration:" + " querySnapshot=" + querySnapshot, e);

                if (querySnapshot != null && e == null) {
                    if (querySnapshot.isEmpty()) {
                        endReached = true;
                    } else {
                        contactList.clear();
                        contactList.addAll(new Jarvis<UserProfile>().tr(querySnapshot, new Contact()));
                        mAdapter.notifyDataSetChanged();
                        Log.i("contactList", contactList.toString());
                        lastQuerySnapshot = querySnapshot;
                        lastDisplayedPage = 0;
                    }

                    //Indicate the search's result status
                    indicationsLayout.setVisibility(contactList.size() == 0 ? View.VISIBLE : View.GONE);

                    // Scroll the list back to the top.
                    mRecyclerView.scrollToPosition(0);
                } else
                    __.showShortToast(getContext(), getString(R.string.load_error_message));

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private synchronized void loadMore() {
        Query loadMoreQuery = mLoadQuery.startAfter(lastQuerySnapshot.getDocuments().get(lastQuerySnapshot.size() - 1));
        ++lastRequestedPage;
        loadMoreQuery.limit(HITS_PER_PAGE).get().addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot querySnapshot = task.getResult();
                Exception e = task.getException();

                Log.d(TAG, "loadMore::" + "querySnapshot=" + querySnapshot + " e=", e);

                if (task.isSuccessful() && querySnapshot != null && e == null) {
                    if (querySnapshot.isEmpty()) {
                        endReached = true;
                    } else {
                        contactList.addAll(new Jarvis<UserProfile>().tr(querySnapshot, new Contact()));
                        mAdapter.notifyDataSetChanged();
                        lastQuerySnapshot = querySnapshot;
                        lastDisplayedPage = lastRequestedPage;
                    }
                } else
                    __.showShortToast(getContext(), getString(R.string.load_error_message));
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


    private synchronized void unsync() {
        if (contactsRegistration != null) contactsRegistration.remove();
    }


    @Override
    public void onStart() {
        super.onStart();
        sync();
    }

    @Override
    public void onStop() {
        super.onStop();
        unsync();
    }
}