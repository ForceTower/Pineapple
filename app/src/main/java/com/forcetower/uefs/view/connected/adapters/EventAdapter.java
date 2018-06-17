package com.forcetower.uefs.view.connected.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemCardEventBinding;
import com.forcetower.uefs.db_service.entity.Event;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by João Paulo on 16/06/2018.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {
    private final List<Event> events;

    public EventAdapter() {
        events = new ArrayList<>();
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCardEventBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_card_event, parent, false);
        return new EventHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        holder.bind(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setEvents(List<Event> events) {
        this.events.clear();
        this.events.addAll(events);

        Timber.d("Received list: " + events);

        notifyDataSetChanged();
    }

    class EventHolder extends RecyclerView.ViewHolder {
        private final ItemCardEventBinding binding;

        EventHolder(ItemCardEventBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Event event) {
            binding.setEvent(event);
            binding.executePendingBindings();
        }
    }
}
