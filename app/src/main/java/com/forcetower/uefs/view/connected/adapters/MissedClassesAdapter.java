package com.forcetower.uefs.view.connected.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemMissedClassBinding;
import com.forcetower.uefs.db.entity.DisciplineMissedClass;
import com.forcetower.uefs.view.BaseDiffCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 23/06/2018.
 */
public class MissedClassesAdapter extends RecyclerView.Adapter<MissedClassesAdapter.MissedHolder> {
    private final List<DisciplineMissedClass> items;

    public MissedClassesAdapter() {
        items = new ArrayList<>();
    }

    public void setItems(List<DisciplineMissedClass> items) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new BaseDiffCallback<>(this.items, items));
        result.dispatchUpdatesTo(this);

        this.items.clear();
        this.items.addAll(items);
    }

    @NonNull
    @Override
    public MissedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMissedClassBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_missed_class, parent, false);
        return new MissedHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MissedHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MissedHolder extends RecyclerView.ViewHolder {
        private final ItemMissedClassBinding binding;

        MissedHolder(ItemMissedClassBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DisciplineMissedClass missed) {
            binding.setMissed(missed);
            binding.executePendingBindings();
        }
    }
}
