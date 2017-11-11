package com.forcetower.uefs.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.model.UClassDay;

import java.util.HashMap;
import java.util.List;

/**
 * Created by João Paulo on 11/11/2017.
 * TODO uncompleted
 */
public class WeekScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private HashMap<String, List<UClassDay>> classesPerDay;

    public WeekScheduleAdapter(Context context, HashMap<String, List<UClassDay>> classesPerDay) {
        this.context = context;
        this.classesPerDay = classesPerDay;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_day_item, parent, false);
        return new DayScheduleHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DayScheduleHolder viewHolder = (DayScheduleHolder) holder;

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        viewHolder.rv_schedule_day.setLayoutManager(layoutManager);

        String day = Utils.getDayOfWeek(position + 1);
        List<UClassDay> dayClasses = classesPerDay.get(day);

        DayScheduleAdapter dayScheduleAdapter = new DayScheduleAdapter(context, dayClasses, day);
        viewHolder.rv_schedule_day.setAdapter(dayScheduleAdapter);
    }

    @Override
    public int getItemCount() {
        return classesPerDay.values().size();
    }

    private class DayScheduleHolder extends RecyclerView.ViewHolder {
        RecyclerView rv_schedule_day;

        DayScheduleHolder(View view) {
            super(view);
            rv_schedule_day = view.findViewById(R.id.rv_schedule_day);
        }
    }
}
