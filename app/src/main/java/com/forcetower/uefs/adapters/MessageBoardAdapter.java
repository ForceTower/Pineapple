package com.forcetower.uefs.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;

import java.util.List;

/**
 * Created by João Paulo on 18/11/2017.
 */

public class MessageBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public interface OnMessageClickListener {
        void onMessageClicked(View view, int position, SagresMessage message);
    }

    private Context context;
    private List<SagresMessage> messages;
    private OnMessageClickListener onMessageClickListener;

    public MessageBoardAdapter(Context context, List<SagresMessage> messages) {
        this.context = context;
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void setOnMessageClickListener(OnMessageClickListener onMessageClickListener) {
        this.onMessageClickListener = onMessageClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_list_item, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindMessageViewHolder((MessageHolder)holder, position);
    }

    private void onBindMessageViewHolder(MessageHolder holder, int position) {
        SagresMessage message = getItem(position);
        if (message == null) {
            holder.tv_class_name.setText(context.getString(R.string.this_is_and_error));
            return;
        }

        String combined = message.getReceivedTime() + " - " + message.getSender();
        holder.tv_class_name.setText(message.getClassName());
        holder.tv_message.setText(message.getMessage());
        holder.tv_sender.setText(combined);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private SagresMessage getItem(int position) {
        if (position < 0 || position >= messages.size())
            return null;

        return messages.get(position);
    }

    public class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_class_name;
        TextView tv_message;
        TextView tv_sender;

        MessageHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            tv_class_name = itemView.findViewById(R.id.tv_class_name);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_sender = itemView.findViewById(R.id.tv_sender);
        }

        @Override
        public void onClick(View view) {
            if (onMessageClickListener != null) {
                int position = getAdapterPosition();
                onMessageClickListener.onMessageClicked(view, position, getItem(position));
            }
        }
    }
}
