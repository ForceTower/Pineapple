package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.CardDisciplineClassesBinding;
import com.forcetower.uefs.db.entity.DisciplineClassItem;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.OnClassClickListener;

import java.util.List;

import timber.log.Timber;

/**
 * Created by João Paulo on 10/03/2018.
 */

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ClassHolder> {
    private final Context context;
    private final List<DisciplineClassItem> classItems;
    private final RecyclerView.RecycledViewPool recycledViewPool;
    private int exPosition = -1;
    private OnClassClickListener classClickListener;


    public ClassesAdapter(Context context, List<DisciplineClassItem> classItems) {
        this.context = context;
        this.classItems = classItems;
        this.recycledViewPool = new RecyclerView.RecycledViewPool();
    }

    public void setOnClassClickListener(OnClassClickListener classClickListener) {
        this.classClickListener = classClickListener;
    }

    @NonNull
    @Override
    public ClassHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardDisciplineClassesBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.card_discipline_classes, parent, false);
        return new ClassHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassHolder holder, int position) {
        holder.bind(classItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return classItems.size();
    }

    public void setClasses(List<DisciplineClassItem> classItems) {
        this.classItems.clear();
        this.classItems.addAll(classItems);
        notifyDataSetChanged();
    }

    class ClassHolder extends RecyclerView.ViewHolder {
        private final CardDisciplineClassesBinding binding;
        private final MaterialAdapter adapter;
        private boolean canExpand = true;

        ClassHolder(CardDisciplineClassesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(v -> onClassClicked());

            binding.rvSupportMaterial.setRecycledViewPool(recycledViewPool);
            adapter = new MaterialAdapter(context);
            binding.rvSupportMaterial.setAdapter(adapter);
            binding.rvSupportMaterial.setLayoutManager(new LinearLayoutManager(context));
        }

        private void onClassClicked() {
            int position = getAdapterPosition();
            DisciplineClassItem classItem = classItems.get(position);
            canExpand = classItem.getMaterials() != null && classItem.getMaterials().size() > 0;
            Timber.d("Can expand: %s", canExpand);
            Timber.d("Clicked class id: %d <> Number: %d", classItem.getUid(), classItem.getNumber());

            if (canExpand) {
                if (VersionUtils.isLollipop()) {
                    boolean isExpanded = position == exPosition;
                    exPosition = isExpanded ? -1 : position;
                    TransitionManager.beginDelayedTransition(binding.viewRoot);
                    notifyDataSetChanged();
                } else {
                    if (classClickListener != null)
                        classClickListener.onClassClicked(classItem, position);
                }
            }
        }

        public void bind(DisciplineClassItem item, int position) {
            canExpand = item.getMaterials() != null && item.getMaterials().size() > 0;
            String subject = item.getSubject();
            if (subject == null || subject.trim().isEmpty()) subject = "???";

            String situation = item.getSituation();
            if (situation == null || situation.trim().isEmpty()) situation = "Não cadastrada";

            String date = item.getDate();
            if (date == null || date.trim().isEmpty()) date = "???";

            int attachments = item.getNumberOfMaterials();

            int resId = R.drawable.ic_clear_black_24dp;
            int color = android.R.color.holo_red_dark;
            int type = 0;

            if (situation.equalsIgnoreCase("Realizada")) {
                resId = R.drawable.ic_check_black_24dp;
                color = android.R.color.holo_green_light;
                type = 1;
            } else if (situation.equalsIgnoreCase("Planejada")) {
                resId = R.drawable.ic_circle_black_24dp;
                color = android.R.color.holo_blue_dark;
                type = 2;
            }

            binding.tvClassSubject.setText(subject);
            binding.tvClassAttachments.setText(context.getString(R.string.attachments, attachments));
            binding.tvClassDate.setText(date);
            binding.tvClassSituation.setText(situation);
            binding.ivClassSituation.setColorFilter(context.getResources().getColor(color));
            binding.ivClassSituation.setImageResource(resId);

            if (type == 0) {
                binding.llInformation.setVisibility(View.GONE);
            } else {
                binding.llInformation.setVisibility(View.VISIBLE);
            }

            adapter.setMaterials(item.getMaterials());

            if (VersionUtils.isLollipop()) {
                boolean isExpanded = exPosition == position;
                binding.rlExpanded.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull ClassHolder holder) {
        Timber.e("Failed to recycle class holder");
        return super.onFailedToRecycleView(holder);
    }
}
