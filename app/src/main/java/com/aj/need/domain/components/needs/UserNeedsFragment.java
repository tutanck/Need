package com.aj.need.domain.components.needs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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

import com.aj.need.db.colls.itf.Coll;
import com.aj.need.tools.components.others._Recycler;
import com.aj.need.tools.utils.Jarvis;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class UserNeedsFragment extends Fragment {

    private final static String TAG = "UserNeedsFrag";

    // Constants:
    /*
    *!important the number of results displayed must always be enough to over-fulfill the screen :
    * The first visible and the last visibles items must never be seen on the same screen
    * */
    private final int HITS_PER_PAGE = 10; //// TODO: 27/10/2017  20 in prod

    // Number of items before the end of the list past which we start loading more content.
    private static final int LOAD_MORE_THRESHOLD = 1;//// TODO: 15/11/2017 5 in prod


    // UI:
    private RecyclerView mRecyclerView;
    private UserNeedsRecyclerAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;
    private List<UserNeed> needList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private LinearLayout indicationsLayout;


    // Search:
    private Query mLoadQuery;
    private QuerySnapshot lastQuerySnapshot;
    private int lastSyncedSeqNo = 0;

    private ListenerRegistration needsRegistration;


    private boolean isLoading;


    // Pagination:
    private Boolean pageRequestInProgress = false;


    public static UserNeedsFragment newInstance() {
        return new UserNeedsFragment();
    }


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_recycler_view_with_fab, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(linearLayoutManager = new LinearLayoutManager(getActivity()));
        mAdapter = new UserNeedsRecyclerAdapter(getContext(), needList);
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
        indicationTV1.setText(R.string.fragment_user_need_indic1);
        indicationTV2.setText(R.string.fragment_user_need_indic2);

        FloatingActionButton fab = view.findViewById(R.id.fab_recycler_action);
        fab.setImageResource(R.drawable.ic_search_24dp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserNeedNewSearchActivity.start(getContext());
            }
        });

        mLoadQuery = USER_NEEDS.getCurrentUserNeedsRef()
                .whereEqualTo(USER_NEEDS.deletedKey, false) //// TODO: 27/10/2017  dt work well on disactivating needs
                .orderBy(USER_NEEDS.activeKey, Query.Direction.DESCENDING)
                .orderBy(Coll.dateKey, Query.Direction.DESCENDING);

        setRecyclerViewScrollListener();
        setRecyclerViewItemTouchListener();

        return view;
    }


    public void sync() {
        mSwipeRefreshLayout.setRefreshing(true);

        unsync();
        needsRegistration = mLoadQuery.limit(HITS_PER_PAGE).addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e
            ) {
                Log.i(TAG, "needsRegistration:" + " querySnapshot=" + querySnapshot + " e=", e);

                if (querySnapshot != null && e == null) {
                    // refresh ui
                    refreshList(querySnapshot, true, null);

                    //Indicate the search's result status
                    indicationsLayout.setVisibility(needList.size() == 0 ? View.VISIBLE : View.GONE);

                    // Scroll the list back to the top.
                    mRecyclerView.scrollToPosition(0);
                } else
                    __.showShortToast(getActivity(), getString(R.string.load_error_message));

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private synchronized void refreshList(QuerySnapshot querySnapshot, boolean reset, Integer currentSyncedSeqNo) {
        if (querySnapshot == null) return;
        lastQuerySnapshot = querySnapshot;

        if (reset) {
            lastSyncedSeqNo++;
            needList.clear();
        } else // Ignore results if they are for an older synced data
            if (currentSyncedSeqNo != lastSyncedSeqNo) return;

        needList.addAll(new Jarvis<UserNeed>().tr(querySnapshot, new UserNeed()));
        mAdapter.notifyDataSetChanged();
        Log.i(TAG, "needList" + needList.toString());
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


    private void setRecyclerViewItemTouchListener() {
        mRecyclerView.addOnItemTouchListener(new _Recycler.ItemTouchListener(
                getContext(), mRecyclerView, new _Recycler.ClickListener() {

            @Override
            public void onClick(RecyclerView.ViewHolder viewHolder, int position) {
                UserNeed userNeed = ((UserNeedsRecyclerAdapter.ViewHolder) viewHolder).getUserNeed();
                UserNeedActivity.start(getContext(), userNeed.get_id(), userNeed.getTitle(), userNeed.getSearch());
            }

            @Override
            public void onLongClick(final RecyclerView.ViewHolder viewHolder, int position) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Supprimer le besoin ?");
                builder.setMessage(getString(R.string.delete_need_warning));

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((UserNeedsRecyclerAdapter.ViewHolder) viewHolder).deleteNeed(getActivity(), UserNeedsFragment.this,
                                IO.getCurrentUserUid(), needList, mAdapter);
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


    public synchronized void unsync() {
        if (needsRegistration != null) needsRegistration.remove();
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