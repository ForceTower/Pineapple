package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.DisciplineClassMaterialLink;
import com.forcetower.uefs.util.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class_support_material, parent, false);
        return new MaterialHolder(view);
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
        @BindView(R.id.tv_material_name)
        TextView tvMaterialName;
        private String link;

        MaterialHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(v -> onItemClick());
        }

        private void onItemClick() {
            NetworkUtils.openLink(context, link);
            /*int position = getAdapterPosition();
            Timber.d("Position clicked %d", position);
            if (onMaterialLinkClickListener != null)
                onMaterialLinkClickListener.onMaterialLinkClick(materials.get(position), position);*/
        }

        public void bind(DisciplineClassMaterialLink material) {
            tvMaterialName.setText(material.getName());
            link = material.getLink();
        }
    }
}
