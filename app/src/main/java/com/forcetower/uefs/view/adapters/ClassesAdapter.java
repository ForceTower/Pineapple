package com.forcetower.uefs.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassItem;

import java.util.List;

/**
 * Created by João Paulo on 22/12/2017.
 */

public class ClassesAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<SagresClassItem> classes;

    public ClassesAdapter(Context context, List<SagresClassItem> classes) {
        this.context = context;
        this.classes = classes;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_class_item_card, parent, false);
        return new ClassHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ClassHolder classHolder = (ClassHolder) holder;
        SagresClassItem item = classes.get(position);

        String subject = item.getSubject();
        if (subject == null || subject.trim().isEmpty()) subject = "???";

        String situation = item.getSituation();
        if (situation == null || situation.trim().isEmpty()) situation = "Não cadastrada";

        String date = item.getDate();
        if (date == null || date.trim().isEmpty()) date = "???";

        String attachments = item.getNumberOfMaterials();
        if (attachments == null || attachments.trim().isEmpty()) attachments = "0";


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

        classHolder.tv_class_subject.setText(subject);
        classHolder.tv_class_attachments.setText(attachments);
        classHolder.tv_class_date.setText(date);
        classHolder.tv_class_situation.setText(situation);
        classHolder.iv_class_situation.setColorFilter(context.getResources().getColor(color));
        classHolder.iv_class_situation.setImageResource(resId);

        if (type == 0) {
            classHolder.ll_information.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return classes.size();
    }

    public void setClasses(List<SagresClassItem> classes) {
        this.classes = classes;
        notifyDataSetChanged();
    }

    class ClassHolder extends RecyclerView.ViewHolder {
        TextView tv_class_subject;
        TextView tv_class_situation;
        TextView tv_class_date;
        TextView tv_class_attachments;
        ImageView iv_class_situation;
        LinearLayout ll_information;

        ClassHolder(View itemView) {
            super(itemView);

            tv_class_subject = itemView.findViewById(R.id.tv_class_subject);
            tv_class_situation = itemView.findViewById(R.id.tv_class_situation);
            tv_class_date = itemView.findViewById(R.id.tv_class_date);
            tv_class_attachments = itemView.findViewById(R.id.tv_class_attachments);
            iv_class_situation = itemView.findViewById(R.id.iv_class_situation);
            ll_information = itemView.findViewById(R.id.ll_information);
        }
    }
}
