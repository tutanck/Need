package com.aj.need.domain.components.messages;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aj.need.R;
import com.aj.need.db.colls.MESSAGES;
import com.aj.need.db.colls.USER_CONTACTS;
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.domain.components.profile.UserProfile;
import com.aj.need.domain.components.profile.UserProfilesRecyclerAdapter;
import com.aj.need.tools.components.fragments.ProgressBarFragment;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadContacts();
    }


    private void loadContacts() {  //// TODO: 10/10/2017  redo
        progressBarFragment.show();

        USER_CONTACTS.computeCurrentUserContacts()
                .addOnSuccessListener(new OnSuccessListener<List<UserProfile>>() {
                    @Override
                    public void onSuccess(List<UserProfile> result) {
                        Log.d("TRonSuccess", "Transaction success: " + result);
                        mProfiles.clear();

                        for (UserProfile uc : result)
                            mProfiles.add(uc);


                        indicationsLayout.setVisibility(mProfiles.size() == 0 ? View.VISIBLE : View.GONE);
                        mAdapter.notifyDataSetChanged();
                        progressBarFragment.hide();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TRonFailure", "Transaction failure.", e);
                        //// TODO: 15/10/2017
                    }
                });




        /*USER_CONTACTS.getCurrentUserContactsRef().get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            mProfiles.clear();
                            for (DocumentSnapshot contact : task.getResult()) {
                                Log.d("LOL", contact.getId() + " => " + contact.getData());

                                mProfiles.add(new UserProfile(contact.getId(), "todo", 0, 0
                                                , contact.getString(MESSAGES.messageKey)
                                                , contact.getDate(Coll.dateKey).toString() //// TODO: 15/10/2017  date iof str
                                        )
                                );
                            }
                            indicationsLayout.setVisibility(mProfiles.size() == 0 ? View.VISIBLE : View.GONE);
                            mAdapter.notifyDataSetChanged();
                            progressBarFragment.hide();
                        } else {
                            __.showShortToast(getContext(), "Impossible de charger les contacts");
                            //// TODO: 15/10/2017
                        }
                    }
                });*/
    }
}