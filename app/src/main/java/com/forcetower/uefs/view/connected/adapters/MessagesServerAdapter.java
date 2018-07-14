package com.forcetower.uefs.view.connected.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemServerMessageBinding;
import com.forcetower.uefs.db.entity.MessageUNES;

import java.util.ArrayList;
import java.util.List;

public class MessagesServerAdapter extends RecyclerView.Adapter<MessagesServerAdapter.MessageHolder> {
    private final List<MessageUNES> messages;
    private MessageClickListener onClickListener;

    public MessagesServerAdapter() {
        messages = new ArrayList<>();
    }

    public void setOnClickListener(MessageClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setMessages(List<MessageUNES> messages) {
        MessagesDiff diff = new MessagesDiff(this.messages, messages);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diff, true);
        result.dispatchUpdatesTo(this);

        this.messages.clear();
        this.messages.addAll(messages);
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemServerMessageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_server_message, parent, false);
        return new MessageHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        private final ItemServerMessageBinding binding;

        MessageHolder(ItemServerMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            this.binding.getRoot().setOnClickListener(v -> onClick());
        }

        private void onClick() {
            int position = getAdapterPosition();
            MessageUNES message = messages.get(position);
            if (onClickListener != null) onClickListener.onMessageClicked(message);
        }

        void bind(MessageUNES message) {
            binding.setMessage(message);
            binding.executePendingBindings();
        }
    }

    private class MessagesDiff extends DiffUtil.Callback {
        private final List<MessageUNES> oldList;
        private final List<MessageUNES> newList;

        MessagesDiff(List<MessageUNES> oldList, List<MessageUNES> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getUid() == newList.get(newItemPosition).getUid();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }

    public interface MessageClickListener {
        void onMessageClicked(MessageUNES message);
    }
}
