package com.forcetower.uefs.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;

import java.util.List;

/**
 * Created by João Paulo on 18/11/2017.
 */

public class MessageBoardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<SagresMessage> messages;

    public MessageBoardAdapter(Context context, List<SagresMessage> messages) {
        this.context = context;
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.message_list_item_new, parent, false);
        return new MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindMessageViewHolder((MessageHolder) holder, position);
    }

    private void onBindMessageViewHolder(MessageHolder holder, int position) {
        SagresMessage message = getItem(position);
        if (message == null) {
            holder.tv_class_name.setText(context.getString(R.string.this_is_and_error));
            return;
        }

        if (message.getClassName().equalsIgnoreCase("UEFS")) {
            holder.iv_icon.setImageResource(R.drawable.ic_university_accent_30dp);
        } else {
            holder.iv_icon.setImageResource(R.drawable.ic_class_message_accent_30dp);
        }

        holder.tv_class_name.setText(message.getClassName());
        holder.tv_message.setText(message.getMessage());
        holder.tv_sender.setText(message.getSender());
        holder.tv_date_received.setText(message.getReceivedTime());
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

    public void setMessageList(List<SagresMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public interface OnMessageClickListener {
        void onMessageClicked(View view, int position, SagresMessage message);
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        TextView tv_class_name;
        TextView tv_message;
        TextView tv_sender;
        TextView tv_date_received;
        ImageView iv_icon;

        MessageHolder(View itemView) {
            super(itemView);

            tv_class_name = itemView.findViewById(R.id.cod_class);
            tv_message = itemView.findViewById(R.id.message);
            tv_sender = itemView.findViewById(R.id.sender);
            tv_date_received = itemView.findViewById(R.id.date_received);
            iv_icon = itemView.findViewById(R.id.sender_icon);
        }
    }
}
