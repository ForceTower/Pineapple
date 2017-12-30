package com.forcetower.uefs.view.class_details;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.database.entities.ATodoItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 30/12/2017.
 */

public class TodoListAdapter extends RecyclerView.Adapter {
    private List<ATodoItem> items;
    private TodoListFragment.OnTodoItemClickedListener onClickListener;

    public TodoListAdapter(List<ATodoItem> items) {
        setItems(items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_list_item, parent, false);
        return new TodoHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ATodoItem item = items.get(position);
        TodoHolder todo = (TodoHolder) holder;

        todo.cbCompleted.setChecked(item.isCompleted());
        todo.tvTaskName.setText(item.getTitle());
        if (item.hasTimeLimit()) {
            todo.tvDateLimit.setVisibility(View.VISIBLE);
            todo.tvDateLimit.setText(item.getDate());
        }
        else todo.tvDateLimit.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<ATodoItem> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size())
            this.items.remove(position);

        notifyItemRemoved(position);
    }

    public void addItem(ATodoItem item) {
        this.items.add(0, item);
        notifyItemInserted(0);
    }

    public void setOnClickListener(TodoListFragment.OnTodoItemClickedListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class TodoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tv_task_name)
        TextView tvTaskName;
        @BindView(R.id.tv_task_day_limit)
        TextView tvDateLimit;
        @BindView(R.id.cb_finished)
        CheckBox cbCompleted;

        TodoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onClickListener != null) {
                int position = getAdapterPosition();
                onClickListener.onItemClicked(v, position);
            }
        }
    }
}
