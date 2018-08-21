package com.forcetower.uefs.view.connected.adapters;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemSyncRegistryBinding;
import com.forcetower.uefs.db.entity.SyncRegistry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 23/06/2018.
 */
public class SyncRegistryAdapter extends RecyclerView.Adapter<SyncRegistryAdapter.RegistryHolder> {
    private final List<SyncRegistry> registries;

    public SyncRegistryAdapter() {
        this.registries = new ArrayList<>();
    }

    public void setRegistries(List<SyncRegistry> registries) {
        RegisterDiff diff = new RegisterDiff(this.registries, registries);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(diff, true);
        result.dispatchUpdatesTo(this);

        this.registries.clear();
        this.registries.addAll(registries);
    }

    @NonNull
    @Override
    public RegistryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSyncRegistryBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_sync_registry, parent, false);
        return new RegistryHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistryHolder holder, int position) {
        holder.bind(registries.get(position));
    }

    @Override
    public int getItemCount() {
        return registries.size();
    }

    class RegistryHolder extends RecyclerView.ViewHolder {
        private final ItemSyncRegistryBinding binding;

        RegistryHolder(ItemSyncRegistryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SyncRegistry registry) {
            binding.setRegistry(registry);
            binding.executePendingBindings();
        }
    }

    class RegisterDiff extends DiffUtil.Callback {
        private final List<SyncRegistry> oldList;
        private final List<SyncRegistry> newList;

        RegisterDiff(List<SyncRegistry> oldList, List<SyncRegistry> newList) {
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
}
