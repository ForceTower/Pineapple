package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.DisciplineClassItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 10/03/2018.
 */

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ClassHolder> {
    private final Context context;
    private final List<DisciplineClassItem> classItems;

    public ClassesAdapter(Context context, List<DisciplineClassItem> classItems) {
        this.context = context;
        this.classItems = classItems;
    }

    @Override
    public ClassHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_discipline_classes, parent, false);
        return new ClassHolder(view);
    }

    @Override
    public void onBindViewHolder(ClassHolder holder, int position) {
        holder.bind(classItems.get(position));
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

        ClassHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(DisciplineClassItem item) {
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
            }
        }
    }
}
