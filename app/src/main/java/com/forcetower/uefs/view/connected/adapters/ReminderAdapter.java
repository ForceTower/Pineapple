package com.forcetower.uefs.view.connected.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemReminderBinding;
import com.forcetower.uefs.db.entity.TodoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 22/06/2018.
 */
public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderHolder> {
    private final List<TodoItem> reminders;

    public ReminderAdapter() {
        reminders = new ArrayList<>();
    }

    public void setReminders(List<TodoItem> reminders) {
        ReminderDiff diff = new ReminderDiff(this.reminders, reminders);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diff, true);
        result.dispatchUpdatesTo(this);

        this.reminders.clear();
        this.reminders.addAll(reminders);
    }

    @NonNull
    @Override
    public ReminderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReminderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_reminder, parent, false);
        return new ReminderHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderHolder holder, int position) {
        holder.bind(reminders.get(position));
    }

    @Override
    public int getItemCount() {
        return reminders.size();
    }

    class ReminderHolder extends RecyclerView.ViewHolder {
        private final ItemReminderBinding binding;

        ReminderHolder(ItemReminderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TodoItem item) {
            binding.setReminder(item);
        }
    }

    class ReminderDiff extends DiffUtil.Callback {
        private final List<TodoItem> oldList;
        private final List<TodoItem> newList;

        ReminderDiff(List<TodoItem> oldList, List<TodoItem> newList) {
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

        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return super.getChangePayload(oldItemPosition, newItemPosition);
        }
    }
}
