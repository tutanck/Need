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
import com.aj.need.db.colls.itf.Coll;
import com.aj.need.domain.components.profile.UtherProfileActivity;
import com.aj.need.main.A;
import com.aj.need.tools.utils.__;
import com.aj.need.tools.regina.ack.UIAck;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class MessagesActivity extends AppCompatActivity {

    private final static String CONTACT_ID = "CONTACT_ID";
    private final static String CONTACT_NAME = "CONTACT_NAME";
    private final static String CONVERSATION_ID = "CONVERSATION_ID";

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MessageRecyclerAdapter mAdapter;

    private String contact_id = null;
    private String contact_name = null;
    private String conversation_id = null;

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
        conversation_id = getIntent().getStringExtra(CONVERSATION_ID);

        getSupportActionBar().setTitle(contact_name);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_person_24dp);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        final EditText chatboxET = findViewById(R.id.chatbox_et);
        Button chatboxSendBtn = findViewById(R.id.chatbox_send_btn);

        chatboxSendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String msg = chatboxET.getText().toString();
                        if (TextUtils.isEmpty(msg)) return;
                        sendMessage(msg);
                        chatboxET.setText("");
                    }
                }
        );

    }

    public static void start(Context context, String _id, String username, String conversationID) {
        Intent intent = new Intent(context, MessagesActivity.class);
        intent.putExtra(CONTACT_ID, _id);
        intent.putExtra(CONTACT_NAME, username);
        intent.putExtra(CONVERSATION_ID, conversationID);
        context.startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();
        loadMessages();


        MESSAGES.getMESSAGESRef()
                .whereEqualTo(MESSAGES.conversationIDKey, conversation_id)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("messagesListener", "Listen failed.", e);
                            return;
                        }
                        loadMessages();
                    }
                });
    }

    @Override
    public void onStop() {
        super.onStop();
        //// TODO: 15/10/2017
/*
        IO.socket.off(MESSAGES.collTag + contact_id + "/" + A.user_id(this));
        IO.socket.off(MESSAGES.collTag + A.user_id(this) + "/" + contact_id);
    */
    }


    private void sendMessage(String text) {
        MESSAGES.sendMessage(contact_id, text, conversation_id,
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        __.showShortToast(MessagesActivity.this, "Message envoyé");//// TODO: 15/10/2017
                    }
                }
                , new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        __.showLongToast(MessagesActivity.this, "Message non envoyé. cause : " + e);//// TODO: 15/10/2017
                    }
                }
        );
    }


    private void loadMessages() {

        if (conversation_id == null) return;
        MESSAGES.getMESSAGESRef()
                .whereEqualTo(MESSAGES.conversationIDKey, conversation_id).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            messageList.clear();

                            for (DocumentSnapshot message : task.getResult()) {
                                Log.d("loadMessages", message.getId() + " => " + message.getData());
                                //// TODO: 15/10/2017
                                messageList.add(new Message(
                                        message.getString(MESSAGES.messageKey)
                                        , message.getString(MESSAGES.fromKey)
                                        , message.getString(Coll.dateKey)
                                        , message.getString(MESSAGES.conversationIDKey)
                                        , message.getBoolean(MESSAGES.openKey))
                                );
                            }

                            Log.i("messageList", messageList.toString());
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);

                        } else {
                            Log.d("loadMessages", "Error getting documents: ", task.getException()); //// TODO: 15/10/2017  
                        }
                    }
                });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                UtherProfileActivity.start(this, contact_id);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

}
