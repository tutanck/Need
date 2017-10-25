package com.aj.need.domain.components.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.aj.need.db.colls.USER_CONTACTS;
import com.aj.need.tools.utils.Jarvis;
import com.aj.need.tools.utils.__;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class MessagesActivity extends AppCompatActivity {

    private final static String CONTACT_ID = "CONTACT_ID";
    private final static String CONTACT_NAME = "CONTACT_NAME";

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MessageRecyclerAdapter mAdapter;

    private Button chatboxSendBtn;
    private EditText chatboxET;

    private String contact_id = null;
    private String contact_name = null;
    private String conversation_id = null;

    private Query loadQuery;
    private ListenerRegistration conversationRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        mRecyclerView = findViewById(R.id.reyclerview_message_list);
        linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        mAdapter = new MessageRecyclerAdapter(this, messageList);
        mRecyclerView.setAdapter(mAdapter);

        contact_id = getIntent().getStringExtra(CONTACT_ID);
        contact_name = getIntent().getStringExtra(CONTACT_NAME);
        conversation_id = __.ordered_concat(contact_id, IO.getCurrentUserUid());

        getSupportActionBar().setTitle(contact_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        chatboxET = findViewById(R.id.chatbox_et);
        chatboxSendBtn = findViewById(R.id.chatbox_send_btn);

        loadQuery = MESSAGES.getMESSAGESRef()
                .whereEqualTo(MESSAGES.conversationIDKey, conversation_id)
                .orderBy(MESSAGES.dateKey);

        chatboxSendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String text = chatboxET.getText().toString();
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
        loadQuery.get().addOnCompleteListener(this, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                    reloadMessageList(task.getResult());
                else
                    Log.d("loadMessages", "Error getting documents: ", task.getException()); //// TODO: 15/10/2017 complete but keep this useful log
            }
        });
    }


    private void followConversation() {
        conversationRegistration = loadQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(
                    @Nullable QuerySnapshot value
                    , @Nullable FirebaseFirestoreException e
            ) {
                Log.w("followConversation", "value=" + value + " error=" + e);
                if (e == null)
                    reloadMessageList(value);
            }
        });
    }


    private synchronized void reloadMessageList(QuerySnapshot querySnapshot) {
        messageList.clear();
        messageList.addAll(new Jarvis<Message>().tr(querySnapshot, new Message()));
        Log.i("messageList", messageList.toString());
        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
    }


    public static void start(Context context, String _id, String username) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtra(CONTACT_ID, _id);
        intent.putExtra(CONTACT_NAME, username);
        context.startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        loadMessages();
        // ||
        followConversation();
    }


    @Override
    public void onStop() {
        super.onStop();
        if (conversationRegistration != null)
            conversationRegistration.remove();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        this.onBackPressed();
        return (super.onOptionsItemSelected(menuItem));
    }

}
