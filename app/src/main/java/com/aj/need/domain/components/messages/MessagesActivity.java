package com.aj.need.domain.components.messages;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.aj.need.tools.utils.Avail;
import com.aj.need.tools.utils.Jarvis;
import com.aj.need.tools.utils._Storage;
import com.aj.need.tools.utils.__;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    private final int BATCH_SIZE = 25;

    private final static String CONTACT_ID = "CONTACT_ID",
            CONTACT_NAME = "CONTACT_NAME",
            CONTACT_AVAILABILITY = "CONTACT_AVAILABILITY";

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MessageRecyclerAdapter mAdapter;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private EditText chatboxET;

    private String contact_id;
    private String conversation_id;
    private int contactAvailability;

    private Query mLoadQuery;
    private QuerySnapshot lastQuerySnapshot;

    private CollectionReference messagesRef;
    private DocumentReference userContactRef, userAsContactRef;


    private ListenerRegistration conversationRegistration, contactRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mRecyclerView = findViewById(R.id.reyclerview_message_list);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new MessageRecyclerAdapter(this, messageList, Glide.with(this));
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = findViewById(R.id.message_list_SwipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadMessages();
            }
        });

        contact_id = getIntent().getStringExtra(CONTACT_ID);
        String contact_name = getIntent().getStringExtra(CONTACT_NAME);
        contactAvailability = getIntent().getIntExtra(CONTACT_AVAILABILITY, Avail.UNKNOWN);
        conversation_id = __.ordered_concat(contact_id, IO.getCurrentUserUid());

        messagesRef = MESSAGES.getMESSAGESRef();
        userContactRef = USER_CONTACTS.getCurrentUserContactsRef().document(contact_id);
        userAsContactRef = USER_CONTACTS.getUserContactsRef(contact_id).document(IO.getCurrentUserUid());

        if (contact_name != null) setContactNameAsBarTitle(contact_name);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        chatboxET = findViewById(R.id.chatbox_et);
        chatboxET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus)
                    mRecyclerView.smoothScrollToPosition(0); //// TODO: 26/10/2017 do better
            }
        });

        Button chatboxSendBtn = findViewById(R.id.chatbox_send_btn);
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


        _Storage.loadRef(_Storage.getRef(contact_id))
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mAdapter.setContactImageUri(uri);
                    }
                });

        mLoadQuery = MESSAGES.getMESSAGESRef()
                .whereEqualTo(MESSAGES.conversationIDKey, conversation_id)
                .orderBy(MESSAGES.dateKey, Query.Direction.DESCENDING);
    }


    private void sendMessage(String text) {
        DocumentReference msgRef = messagesRef.document();

        Message msg = new Message
                (text, IO.getCurrentUserUid(), contact_id, conversation_id, msgRef.getId());

        WriteBatch batch = IO.db.batch();
        batch.set(msgRef, msg);
        batch.set(userContactRef, msg);
        batch.set(userAsContactRef, msg);

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
        Query query = mLoadQuery;

        if (lastQuerySnapshot == null) return;//no initial load

        if (lastQuerySnapshot.isEmpty()) { //no more content to load
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        DocumentSnapshot offset = lastQuerySnapshot.getDocuments().get(lastQuerySnapshot.size() - 1);
        Log.d("loadMessages/_offset=", offset.getData().toString()); //debug
        query = query.startAfter(offset);

        query.limit(BATCH_SIZE).get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                //!important : useful log for index issues tracking, etc.
                Log.d("MsgAct/loadMessages", "res=" + task.getResult() + " e=", task.getException());

                if (task.isSuccessful())
                    refreshMessageList(task.getResult(), false);
                else
                    __.showShortToast(MessagesActivity.this, getString(R.string.load_error_message));

                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private synchronized void refreshMessageList(QuerySnapshot querySnapshot, boolean reset) {
        if (querySnapshot == null) return;
        lastQuerySnapshot = querySnapshot;

        if (reset) messageList.clear();

        messageList.addAll(new Jarvis<Message>().tr(querySnapshot, new Message()));

        for (Message message : messageList)
            if (message.getFrom().equals(contact_id)) break;
            else if (message.getTo().equals(contact_id) && message.isRead()) {
                mAdapter.setSentMessageReadOffset(message.getMessageID());
                break;
            }


        final Message mostRecentMsg = messageList.get(0);
        if (reset) //!important : do not re-run on loadMore
            if ((!mostRecentMsg.isRead()/*Optimization*/) && mostRecentMsg.getFrom().equals(contact_id)/*not a sender'message*/)
                IO.db.runTransaction(new Transaction.Function<Void>() {//run the real-time transaction in an fallible context
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                        DocumentSnapshot snapshot = transaction.get(userContactRef);
                        String msgID = snapshot.getString(MESSAGES.messageIDKey);
                        boolean read = msgID.equals(mostRecentMsg.getMessageID());

                        transaction.update(userContactRef, MESSAGES.readKey, read);

                        DocumentReference msgRef = messagesRef.document(mostRecentMsg.getMessageID());
                        transaction.update(msgRef, MESSAGES.readKey, read);

                        return null; // Success
                    }
                });

        Log.i("messageList", messageList.toString());
        mAdapter.notifyDataSetChanged();
        if (reset) mRecyclerView.scrollToPosition(0);
    }


    @Override
    public void onStart() {
        super.onStart();
        //initial load then follow
        conversationRegistration = mLoadQuery.limit(BATCH_SIZE)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(
                            @Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e
                    ) {
                        Log.w("MessagesActivity", "conversationRegistration : querySnapshot=" + querySnapshot + " error=" + e);

                        if (e != null || querySnapshot == null) {
                            __.showShortToast(MessagesActivity.this, getString(R.string.load_error_message));
                            return;
                        }
                        if (querySnapshot.isEmpty()) return;

                        refreshMessageList(querySnapshot, true);
                    }
                });
        // ||
        //initial load then follow
        contactRegistration = USERS.getUserRef(contact_id).
                addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e == null && snapshot != null && snapshot.exists()) {
                            Log.d("MessagesActivity", "contactRegistration's snapshot : " + snapshot.getData().toString());
                            setContactNameAsBarTitle(snapshot.getString(USERS.usernameKey));
                            resetContactAvail(snapshot.getLong(USERS.availabilityKey));
                            mAdapter.notifyDataSetChanged();
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


    public static void start(Context context, String _id, String username, int availability) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtra(CONTACT_ID, _id);
        intent.putExtra(CONTACT_NAME, username);
        intent.putExtra(CONTACT_AVAILABILITY, availability);
        context.startActivity(intent);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        this.onBackPressed();
        return (super.onOptionsItemSelected(menuItem));
    }


    private void setContactNameAsBarTitle(String contactName) {
        if (contactName != null) getSupportActionBar().setTitle(contactName);
    }

    private void resetContactAvail(Long avail) {
        contactAvailability = (avail == null ? Avail.UNKNOWN : avail.intValue());
    }

    public int getContactAvailability() {
        return contactAvailability;
    }
}
