package com.aj.need.domain.components.messages;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.main.A;

import java.util.List;

public class MessageRecyclerAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Activity mContext;
    private List<Message> mMessageList;

    public MessageRecyclerAdapter(Context context, List<Message> messageList) {
        mContext = (Activity) context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        if (mMessageList.get(position).getSenderID().equals(A.user_id(mContext)))
            return VIEW_TYPE_MESSAGE_SENT;
        else
            return VIEW_TYPE_MESSAGE_RECEIVED;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_MESSAGE_SENT)
            return new SentMessageHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false));
        else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED)
            return new ReceivedMessageHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false));

        throw new RuntimeException("MessageRecyclerAdapter : Unknown Message Type Exception");
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(mMessageList.get(position));
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(mMessageList.get(position));
        }
    }


    public class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        public SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(message.getCreatedAt());
        }
    }


    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView profileImage;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(message.getCreatedAt());

            // Insert the profile image from the URL into the ImageView.
            //_DateUtils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage); // TODO: 29/09/2017
        }
    }
}