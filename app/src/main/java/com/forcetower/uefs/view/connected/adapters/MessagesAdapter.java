package com.forcetower.uefs.view.connected.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemMessageBinding;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.view.connected.MessageClickListener;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class MessagesAdapter extends ListAdapter<Message, MessagesAdapter.MessageHolder> {
    private MessageClickListener onClickListener;

    public MessagesAdapter() {
        super(new DiffUtil.ItemCallback<Message>() {
            @Override
            public boolean areItemsTheSame(Message oldItem, Message newItem) {
                return oldItem.getUid() == newItem.getUid();
            }

            @Override
            public boolean areContentsTheSame(Message oldItem, Message newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMessageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_message, parent, false);
        return new MessageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setOnClickListener(MessageClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        private final ItemMessageBinding binding;

        MessageHolder(ItemMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(view -> onMessageClicked());
        }

        private void onMessageClicked() {
            int position = getAdapterPosition();
            Message message = getItem(position);
            onClickListener.onMessageClicked(message);
        }

        public void bind(Message message) {
            binding.setMessage(message);
            if (message.getClassReceived().equalsIgnoreCase("UEFS")) {
                binding.senderIcon.setImageResource(R.drawable.ic_university_black_24dp);
            } else {
                binding.senderIcon.setImageResource(R.drawable.ic_class_message_black_24dp);
            }
        }
    }
}
