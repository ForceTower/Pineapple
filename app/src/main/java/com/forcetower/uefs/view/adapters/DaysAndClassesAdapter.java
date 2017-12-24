package com.forcetower.uefs.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 23/12/2017.
 */

public class DaysAndClassesAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<SagresClassTime> classTimeList;

    public DaysAndClassesAdapter(Context context, List<SagresClassTime> classTimeList) {
        this.context = context;
        setClassTimeList(classTimeList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_day_and_classes_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Holder view = (Holder) holder;
        SagresClassTime time = classTimeList.get(position);
        String startEnd = time.getStart() + "~" + time.getFinish();

        view.tv_class_day.setText(Utils.toWeekLongDay(context, time.getDay()));
        view.tv_class_time.setText(startEnd);
    }

    @Override
    public int getItemCount() {
        return classTimeList.size();
    }

    public void setClassTimeList(List<SagresClassTime> classTimeList) {
        if (classTimeList != null) this.classTimeList = classTimeList;
        else this.classTimeList = new ArrayList<>();
        notifyDataSetChanged();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView tv_class_day;
        TextView tv_class_time;

        Holder(View itemView) {
            super(itemView);
            tv_class_day = itemView.findViewById(R.id.tv_class_day);
            tv_class_time = itemView.findViewById(R.id.tv_class_time);
        }
    }
}
