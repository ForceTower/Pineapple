package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemCalendarBinding;
import com.forcetower.uefs.db.entity.CalendarItem;

/**
 * Created by João Paulo on 02/12/2017.
 */
public class CalendarAdapter extends ListAdapter<CalendarItem, CalendarAdapter.ItemHolder> {

    public CalendarAdapter() {
        super(new DiffUtil.ItemCallback<CalendarItem>() {
            @Override
            public boolean areItemsTheSame(CalendarItem oldItem, CalendarItem newItem) {
                return oldItem.getUid() == newItem.getUid();
            }

            @Override
            public boolean areContentsTheSame(CalendarItem oldItem, CalendarItem newItem) {
                return oldItem.equals(newItem);
            }
        });
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCalendarBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_calendar, parent, false);
        return new ItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        CalendarItem item = getItem(position);
        if (item != null) holder.bind(item);
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        private final ItemCalendarBinding binding;

        ItemHolder(ItemCalendarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(CalendarItem item) {
            binding.setCalendar(item);
            binding.executePendingBindings();
        }
    }
}