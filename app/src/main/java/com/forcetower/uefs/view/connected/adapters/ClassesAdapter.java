package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.DisciplineClassItem;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.OnClassClickListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_discipline_classes, parent, false);
        return new ClassHolder(view);
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
        @BindView(R.id.view_root)
        ViewGroup viewRoot;
        @BindView(R.id.tv_class_subject)
        TextView tv_class_subject;
        @BindView(R.id.tv_class_situation)
        TextView tv_class_situation;
        @BindView(R.id.tv_class_date)
        TextView tv_class_date;
        @BindView(R.id.tv_class_attachments)
        TextView tv_class_attachments;
        @BindView(R.id.iv_class_situation)
        ImageView iv_class_situation;
        @BindView(R.id.ll_information)
        LinearLayout ll_information;
        @BindView(R.id.rl_expanded)
        RelativeLayout rlExpansion;

        @BindView(R.id.rv_support_material)
        RecyclerView rvSupportMaterial;
        private MaterialAdapter adapter;

        private boolean canExpand = true;

        ClassHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> onClassClicked());
            ButterKnife.bind(this, itemView);
            rvSupportMaterial.setRecycledViewPool(recycledViewPool);
            adapter = new MaterialAdapter(context);
            rvSupportMaterial.setAdapter(adapter);
            rvSupportMaterial.setLayoutManager(new LinearLayoutManager(context));
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
                    TransitionManager.beginDelayedTransition(viewRoot);
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

            tv_class_subject.setText(subject);
            tv_class_attachments.setText(context.getString(R.string.attachments, attachments));
            tv_class_date.setText(date);
            tv_class_situation.setText(situation);
            iv_class_situation.setColorFilter(context.getResources().getColor(color));
            iv_class_situation.setImageResource(resId);

            if (type == 0) {
                ll_information.setVisibility(View.GONE);
            } else {
                ll_information.setVisibility(View.VISIBLE);
            }

            adapter.setMaterials(item.getMaterials());

            if (VersionUtils.isLollipop()) {
                boolean isExpanded = exPosition == position;
                rlExpansion.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            }
        }
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull ClassHolder holder) {
        Timber.e("Failed to recycle class holder");
        return super.onFailedToRecycleView(holder);
    }
}
