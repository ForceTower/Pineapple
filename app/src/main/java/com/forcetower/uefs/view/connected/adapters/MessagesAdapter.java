package com.forcetower.uefs.view.connected.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.view.connected.MessageClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageHolder> {
    private List<Message> messages;
    private MessageClickListener onClickListener;

    public MessagesAdapter(@NonNull List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<Message> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        notifyDataSetChanged();
    }

    public void setOnClickListener(MessageClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cod_class)
        TextView tvClassName;
        @BindView(R.id.message)
        TextView tvMessage;
        @BindView(R.id.sender)
        TextView tvSender;
        @BindView(R.id.date_received)
        TextView tvDateReceived;
        @BindView(R.id.sender_icon)
        ImageView ivIcon;

        MessageHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(view -> onMessageClicked());
        }

        private void onMessageClicked() {
            int position = getAdapterPosition();
            Message message = messages.get(position);
            onClickListener.onMessageClicked(message);
        }

        public void bind(Message message) {
            if (message.getClassReceived().equalsIgnoreCase("UEFS")) {
                ivIcon.setImageResource(R.drawable.ic_university_black_24dp);
            } else {
                ivIcon.setImageResource(R.drawable.ic_class_message_black_24dp);
            }

            tvClassName.setText(message.getClassReceived());
            tvSender.setText(message.getSender());
            tvDateReceived.setText(message.getReceivedAt());

            if (message.getSpannable() != null)
                tvMessage.setText(message.getSpannable(), TextView.BufferType.SPANNABLE);
            else
                tvMessage.setText(message.getMessage());
        }
    }
}
