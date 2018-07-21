package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemClassSupportMaterialBinding;
import com.forcetower.uefs.db.entity.DisciplineClassMaterialLink;
import com.forcetower.uefs.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 14/05/2018.
 */
public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.MaterialHolder> {
    private final List<DisciplineClassMaterialLink> materials;
    private final Context context;

    MaterialAdapter(Context context) {
        this.context = context;
        this.materials = new ArrayList<>();
    }

    @NonNull
    @Override
    public MaterialHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClassSupportMaterialBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_class_support_material, parent, false);
        return new MaterialHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MaterialHolder holder, int position) {
        holder.bind(materials.get(position));
    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    public void setMaterials(List<DisciplineClassMaterialLink> materials) {
        this.materials.clear();
        this.materials.addAll(materials);
        notifyDataSetChanged();
    }

    public class MaterialHolder extends RecyclerView.ViewHolder {
        private final ItemClassSupportMaterialBinding binding;
        private String link;

        MaterialHolder(ItemClassSupportMaterialBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> onItemClick());
        }

        private void onItemClick() {
            NetworkUtils.openLink(context, link);
        }

        public void bind(DisciplineClassMaterialLink material) {
            binding.tvMaterialName.setText(material.getName());
            link = material.getLink();
        }
    }
}
