package com.aj.need.domain.components.messages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.db.colls.MESSAGES;
import com.aj.need.db.colls.USERS;
import com.aj.need.db.colls.USER_CONTACTS;
import com.aj.need.tools.utils.Jarvis;
import com.aj.need.tools.utils.__;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    private final int BATCH_SIZE = 25;

    private final static String CONTACT_ID = "CONTACT_ID";
    private final static String CONTACT_NAME = "CONTACT_NAME";
    private final static String CONTACT_AVAILABILITY = "CONTACT_AVAILABILITY";

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MessageRecyclerAdapter mAdapter;

    SwipeRefreshLayout mSwipeRefreshLayout;

    private Button chatboxSendBtn;
    private EditText chatboxET;

    private String contact_id;
    private String contact_name;
    private String conversation_id;
    private Integer contactAvailability;
    private Bitmap contactImage;

    private Query loadQuery;
    private QuerySnapshot lastQuerySnapshot;


    private ListenerRegistration conversationRegistration, contactRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mRecyclerView = findViewById(R.id.reyclerview_message_list);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new MessageRecyclerAdapter(this, messageList);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = findViewById(R.id.message_list_SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });

        contact_id = getIntent().getStringExtra(CONTACT_ID);
        contact_name = getIntent().getStringExtra(CONTACT_NAME);
        int tmp = getIntent().getIntExtra(CONTACT_AVAILABILITY, -9);
        contactAvailability = tmp == -9 ? null : tmp;
        conversation_id = __.ordered_concat(contact_id, IO.getCurrentUserUid());

        getSupportActionBar().setTitle(contact_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        chatboxET = findViewById(R.id.chatbox_et);
        chatboxSendBtn = findViewById(R.id.chatbox_send_btn);

        loadQuery = MESSAGES.getMESSAGESRef()
                .whereEqualTo(MESSAGES.conversationIDKey, conversation_id)
                .orderBy(MESSAGES.dateKey, Query.Direction.DESCENDING);

        chatboxSendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = chatboxET.getText().toString().trim();
                        if (TextUtils.isEmpty(text)) return;
                        sendMessage(text);
                        chatboxET.setText("");
                    }
                });
    }


    private void sendMessage(String text) {

        Message msg = new Message(text, IO.getCurrentUserUid(), contact_id, conversation_id);

        final DocumentReference msgRef = MESSAGES.getMESSAGESRef().document();
        final DocumentReference senderUcRef = USER_CONTACTS.getCurrentUserContactsRef().document(contact_id);
        final DocumentReference recipientUcRef = USER_CONTACTS.getUserContactsRef(contact_id).document(IO.getCurrentUserUid());

        WriteBatch batch = IO.db.batch();
        batch.set(msgRef, msg);
        batch.set(senderUcRef, msg);
        batch.set(recipientUcRef, msg);

        batch.commit().addOnFailureListener(
                this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        __.showLongToast(MessagesActivity.this, "Erreur d'envoi du message!");
                    }
                }
        );
    }


    private void loadMessages() {
        loadQuery.limit(BATCH_SIZE).get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                    refreshMessageList(task.getResult(), true);
                else//!important : useful comment for index issues tracking
                    Log.d("MsgAct/loadMessages", "Error loading messages : ", task.getException());
            }
        });
    }


    private void followConversation() {
        conversationRegistration = loadQuery.limit(BATCH_SIZE)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(
                            @Nullable QuerySnapshot value
                            , @Nullable FirebaseFirestoreException e
                    ) {
                        Log.w("followConversation", "value=" + value + " error=" + e);
                        if (e == null)
                            refreshMessageList(value, true);
                    }
                });
    }


    private void refresh() { //// TODO: 26/10/2017  fusion wth loadMessages
        if (lastQuerySnapshot == null) {
            //!important (in case of first load error)
            loadMessages();
            return;
        }

        if (lastQuerySnapshot.isEmpty()) {
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        DocumentSnapshot topDocument = lastQuerySnapshot.getDocuments().get(lastQuerySnapshot.size() - 1);
        Log.d("_topDoc", topDocument.getData().toString());

        loadQuery.startAfter(topDocument).limit(BATCH_SIZE).get()
                .addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        refreshMessageList(querySnapshot, false);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
    }


    private synchronized void refreshMessageList(QuerySnapshot querySnapshot, boolean reset) {
        lastQuerySnapshot = querySnapshot;
        if (reset) messageList.clear();
        messageList.addAll(new Jarvis<Message>().tr(querySnapshot, new Message()));
        Log.i("messageList", messageList.toString());
        mAdapter.notifyDataSetChanged();
        if (reset)
            mRecyclerView.scrollToPosition(0 /* mRecyclerView.getAdapter().getItemCount() - 1 //todo rem comment*/);
    }


    public static void start(Context context, String _id, String username, int availability) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtra(CONTACT_ID, _id);
        intent.putExtra(CONTACT_NAME, username);
        intent.putExtra(CONTACT_AVAILABILITY, availability);
        context.startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        loadMessages();
        // ||
        followConversation();
        // ||
        contactRegistration = USERS.getUserRef(contact_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e == null && snapshot != null && snapshot.exists()) {
                    Log.d("MessagesActivity: ", "contactRegistration's snapshot : " + snapshot.getData().toString());
                    contactAvailability = ((Long) snapshot.getData().get(USERS.availabilityKey)).intValue();
                    mAdapter.notifyDataSetChanged(); //// TODO: 25/10/2017 test with 2 devices
                }
            }
        });
    }


    @Override
    public void onStop() {
        super.onStop();
        if (conversationRegistration != null)
            conversationRegistration.remove();

        if (contactRegistration != null)
            contactRegistration.remove();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        this.onBackPressed();
        return (super.onOptionsItemSelected(menuItem));
    }

    public Integer getContactAvailability() {
        return contactAvailability;
    }

    public Bitmap getContactImage() {
        return contactImage;
    }
}
