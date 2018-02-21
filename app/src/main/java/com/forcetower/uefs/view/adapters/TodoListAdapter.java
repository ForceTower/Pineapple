package com.forcetower.uefs.view.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.database.entities.ATodoItem;
import com.forcetower.uefs.view.class_details.TodoListFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 30/12/2017.
 */

public class TodoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ATodoItem> items;
    private TodoListFragment.OnTodoItemClickedListener onClickListener;

    public TodoListAdapter(List<ATodoItem> items) {
        this.items = new ArrayList<>();
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
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnClickListener(TodoListFragment.OnTodoItemClickedListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void insertItem(int position, ATodoItem item) {
        if (items.contains(item))
            return;

        items.add(position, item);
        notifyItemInserted(position);
    }

    public void removeAll() {
        items.clear();
    }

    public void changeItemAtPos(int position, boolean completed) {
        items.get(position).setCompleted(completed);
        notifyItemChanged(position);
    }

    public void insertAll(List<ATodoItem> filtered) {
        items.addAll(filtered);
        notifyDataSetChanged();
    }

    class TodoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tv_task_name)
        TextView tvTaskName;
        @BindView(R.id.tv_task_day_limit)
        TextView tvDateLimit;
        @BindView(R.id.cb_finished)
        CheckBox cbCompleted;
        @BindView(R.id.view_delete_todo)
        View ivDeleteItem;
        @BindView(R.id.center_focus_enable)
        View relativeCenter;

        TodoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            cbCompleted.setOnClickListener(this::onCheck);
            relativeCenter.setOnClickListener(this);
            ivDeleteItem.setOnClickListener(this::delete);
        }

        private void delete(View view) {
            int position = getAdapterPosition();
            ATodoItem item = items.get(position);
            if (onClickListener != null) {
                onClickListener.onItemDelete(view, item, position);
            }
        }

        private void onCheck(View view) {
            boolean checked = cbCompleted.isChecked();
            int position = getAdapterPosition();
            items.get(position).setCompleted(checked);

            if (onClickListener != null) {
                onClickListener.onItemUpdated(view, items.get(position), position);
            }
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            cbCompleted.performClick();

            if (onClickListener != null) {
                onClickListener.onItemClicked(v, position);
            }
        }
    }
}
