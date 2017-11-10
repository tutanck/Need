package com.aj.need.domain.components.messages;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aj.need.R;
import com.aj.need.db.IO;
import com.aj.need.tools.utils.Avail;
import com.aj.need.tools.utils._DateUtils;
import com.aj.need.tools.utils._Storage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

public class MessageRecyclerAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private String sentMessageReadOffsetID;
    private Uri contactImageUri;

    private Activity mContext;
    private List<Message> mMessageList;
    private RequestManager glide;

    public MessageRecyclerAdapter(Context context, List<Message> messageList, RequestManager glide) {
        this.mContext = (Activity) context;
        this.mMessageList = messageList;
        this.glide = glide;
    }

    public void setSentMessageReadOffset(String messageID) {
        this.sentMessageReadOffsetID = messageID;
    }

    public void setContactImageUri(Uri contactImageUri) {
        this.contactImageUri = contactImageUri;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        if (IO.isCurrentUser(mMessageList.get(position).getFrom()))
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
        ImageView profileImage;

        public SentMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            profileImage = itemView.findViewById(R.id.image_message_profile);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(_DateUtils.since(message.getDate()));

            profileImage.setVisibility(message.isPending() ? View.VISIBLE : View.INVISIBLE);

            if (message.getMessageID().equals(sentMessageReadOffsetID)) {
                profileImage.setVisibility(View.VISIBLE);
                glide.load(contactImageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImage);
            }

        }
    }


    public class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;
        ImageView profileImageIV;
        FloatingActionButton statusFab;

        public ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            profileImageIV = itemView.findViewById(R.id.image_message_profile);
            statusFab = itemView.findViewById(R.id.image_message_profile_status);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage());
            timeText.setText(_DateUtils.since(message.getDate()));
            int contactAvailability = ((MessagesActivity) mContext).getContactAvailability();

            statusFab.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(mContext, Avail.getColor(contactAvailability))));
            statusFab.setVisibility(View.VISIBLE);


            //performance issues check : @see https://stackoverflow.com/questions/40301389/imageview-content-loading-from-to-another-imageview
            if (contactImageUri != null)
                glide.load(contactImageUri)
                        .apply(RequestOptions.circleCropTransform())
                        .into(profileImageIV);
        }
    }
}