package com.aj.need.domain.components.needs.userneeds;

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
import com.aj.need.domain.components.needs.UserNeedAdActivity;
import com.aj.need.domain.components.needs.UserNeedNewSearchActivity;

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

    /*
    *!important the number of results displayed must always be enough to over-fulfill the screen :
    * The first visible and the last visibles items must never be seen on the same screen
    * */
    private final int BATCH_SIZE = 10; //// TODO: 27/10/2017  25 in prod

    private boolean isLoading;

    private List<UserNeed> needList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private UserNeedsRecyclerAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private LinearLayout indicationsLayout;

    private UserNeedsFragment self = this;

    private Query mLoadQuery;
    private QuerySnapshot lastQuerySnapshot;

    private ListenerRegistration needsRegistration;


    @Override
    public View onCreateView(
            LayoutInflater inflater
            , ViewGroup container
            , Bundle savedInstanceState
    ) {

        View view = inflater.inflate(R.layout.fragment_user_needs, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new UserNeedsRecyclerAdapter(getContext(), needList);
        mRecyclerView.setAdapter(mAdapter);

        setRecyclerViewScrollListener();
        setRecyclerViewItemTouchListener();

        mSwipeRefreshLayout = view.findViewById(R.id.recycler_view_SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (lastQuerySnapshot == null) loadUserNeeds(null);
                else mSwipeRefreshLayout.setRefreshing(false);
            }
        });


        indicationsLayout = view.findViewById(R.id.component_recycler_indications_layout);

        TextView indicationTV1 = view.findViewById(R.id.indicationTV1);
        indicationTV1.setText(R.string.fragment_user_need_indic1);

        TextView indicationTV2 = view.findViewById(R.id.indicationTV2);
        indicationTV2.setText(R.string.fragment_user_need_indic2);


        FloatingActionButton fab = view.findViewById(R.id.fab_add_need);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserNeedNewSearchActivity.start(getContext());
            }
        });


        mLoadQuery = USER_NEEDS.getCurrentUserNeedsRef()
                .whereEqualTo(USER_NEEDS.deletedKey, false) //// TODO: 27/10/2017  check if work well on disactivating needs
                .orderBy(USER_NEEDS.activeKey, Query.Direction.DESCENDING); //// TODO: 27/10/2017 by date too

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        //initial load then follow
        needsRegistration = mLoadQuery.limit(BATCH_SIZE)
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e
                    ) {
                        Log.w("needsRegistration", "querySnapshot=" + querySnapshot + " error=" + e);
                        if (e == null && querySnapshot != null)
                            refreshUserNeedsList(querySnapshot, true);
                        else
                            __.showShortToast(getActivity(), getString(R.string.load_error_message));
                    }
                });
    }


    private synchronized/*!important : sync access to shared attributes (isLoading, etc) */
    void loadUserNeeds(final DocumentSnapshot offset) {
        if (isLoading)
            return; //cancel concurrent manual reload is better.//TODO: 28/10/2017  test if it's useful or not

        // TODO: 28/10/2017  test if it's useful or not
        isLoading = true;/*!important : must be 1st instruction and only this method should modify it*/
        mSwipeRefreshLayout.setRefreshing(true); //useful only for loadMore

        Query query = mLoadQuery;

        Log.d("loadUserNeeds/_offset=", offset != null ? offset.getData().toString() : " no offset"); //debug

        if (offset != null/*loadMore*/) query = query.startAfter(offset);

        query.limit(BATCH_SIZE).get().addOnCompleteListener(getActivity(), new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //!important : useful log for index issues tracking, etc.
                Log.d("UNeedsFra/loadUserNeeds", "res=" + task.getResult() + " e=", task.getException());

                if (task.isSuccessful())
                    refreshUserNeedsList(task.getResult(), offset == null/*reload*/);
                else
                    __.showShortToast(getContext(), getString(R.string.load_error_message));

                mSwipeRefreshLayout.setRefreshing(false);
                isLoading = false;
            }
        });
    }


    private synchronized void refreshUserNeedsList(QuerySnapshot querySnapshot, boolean reset) {
        if (querySnapshot == null) return;
        lastQuerySnapshot = querySnapshot;
        if (reset) needList.clear();
        needList.addAll(new Jarvis<UserNeed>().tr(querySnapshot, new UserNeed()));
        indicationsLayout.setVisibility(needList.size() == 0 ? View.VISIBLE : View.GONE);
        mAdapter.notifyDataSetChanged();
        if (reset) mRecyclerView.scrollToPosition(0);
    }


    private void setRecyclerViewScrollListener() {
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (lastQuerySnapshot == null /*the initial load is required to load more*/
                        || lastQuerySnapshot.isEmpty() /*no more content to load*/
                        || isLoading /*load in progress*/) return;

                int firstCompletelyVisibleItemPosition = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                int lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
                int totalItemCount = mRecyclerView.getLayoutManager().getItemCount();

                //__.showShortToast(getContext(), "load more: firstVisible=" + firstCompletelyVisibleItemPosition + " lastVisible=" + lastVisibleItemPosition);

                //// TODO: 27/10/2017  fix bug concurent call with reload :: check if all right
                if (firstCompletelyVisibleItemPosition > 0 && totalItemCount == lastVisibleItemPosition + 1) {
                    loadUserNeeds(lastQuerySnapshot.getDocuments().get(lastQuerySnapshot.size() - 1));
                }

            }
        });
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


    @Override
    public void onStop() {
        super.onStop();
        if (needsRegistration != null)
            needsRegistration.remove();
    }

    public static UserNeedsFragment newInstance() {
        return new UserNeedsFragment();
    }
}