package com.aj.need.domain.components.messages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;

public class MessagesActivity extends AppCompatActivity {

    private final static String CONTACT_ID = "CONTACT_ID";
    private final static String CONTACT_NAME = "CONTACT_NAME";

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private MessageRecyclerAdapter mAdapter;

    private String contact_id = null;
    private String contact_name = null;


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

        //// TODO: 15/10/2017
        /*IO.socket.on(MESSAGES.collTag + contact_id + "/" + A.user_id(this), new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                loadMessages();
            }
        });

        IO.socket.on(MESSAGES.collTag + A.user_id(this) + "/" + contact_id, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                loadMessages();
            }
        });*/
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
        MESSAGES.sendMessage(contact_id, text);
    }


    private void loadMessages() {
        MESSAGES.loadMessages(A.user_id(this), contact_id, new UIAck(this) {
            @Override
            protected void onRes(Object res, JSONObject ctx) {
                try {
                    JSONArray jar = (JSONArray) res;
                    messageList.clear();
                    for (int i = 0; i < jar.length(); i++) {
                        JSONObject jo = jar.getJSONObject(i);
                        messageList.add(new Message(jo.getString(MESSAGES.messageKey), jo.getString(MESSAGES.fromKey), jo.getString(Coll.dateKey),true));
                    }
                    Log.i("messageList", messageList.toString());
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.scrollToPosition(mRecyclerView.getAdapter().getItemCount() - 1);
                } catch (JSONException e) {
                    __.fatal(e); //SNO : if a doc exist the Message field should exist too
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
