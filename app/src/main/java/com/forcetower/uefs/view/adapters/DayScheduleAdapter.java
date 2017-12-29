package com.forcetower.uefs.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDay;

import java.util.List;

/**
 * Created by João Paulo on 11/11/2017.
 */

public class DayScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int HEADER = 0, CLASS = 1;
    private Context context;
    private OnClassClickListener clickListener;
    private List<SagresClassDay> classes;
    private String day;

    public DayScheduleAdapter(Context context, List<SagresClassDay> classes, String day) {
        this.context = context;
        this.classes = classes;
        this.day = day;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_class_header, parent, false);
            return new HeaderHolder(view);
        } else if (viewType == CLASS) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_class_item, parent, false);
            return new ClassHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == 0) {
            onBindHeaderViewHolder((HeaderHolder) holder);
        } else {
            onBindClassViewHolder((ClassHolder) holder, position - 1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return HEADER;
        return CLASS;
    }

    private void onBindHeaderViewHolder(HeaderHolder view) {
        view.tv_day.setText(Utils.toWeekLongDay(context, day));
    }

    private void onBindClassViewHolder(ClassHolder view, int position) {
        SagresClassDay uClass = getItem(position);
        if (uClass == null) {
            view.tv_class_name.setText(R.string.this_is_and_error);
            return;
        }

        String name = uClass.getClassName();

        String start = uClass.getStartString();
        String end = uClass.getFinishString();
        String class_time = start + " ~ " + end;


        String campus = uClass.getCampus() != null ? uClass.getCampus() : "-1";
        String place = uClass.getModulo() != null ? uClass.getModulo() : "-1";
        String room = uClass.getAllocatedRoom() != null ? uClass.getAllocatedRoom() : "-1";
        String location = campus + " - " + place + " - " + room;
        if (campus.equalsIgnoreCase("-1") || place.equalsIgnoreCase("-1")) {
            location = room;
        }

        view.tv_class_name.setText(name);
        view.tv_class_time.setText(class_time);
        view.tv_class_location.setText(location);
    }

    private SagresClassDay getItem(int position) {
        if (position < 0 || position >= classes.size())
            return null;
        return classes.get(position);
    }

    @Override
    public int getItemCount() {
        return classes.size() + 1;
    }

    public void setClickListener(OnClassClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnClassClickListener {
        void onClassClicked(View view, int position, SagresClassDay classDay);
    }

    public class HeaderHolder extends RecyclerView.ViewHolder {
        TextView tv_day;

        HeaderHolder(View itemView) {
            super(itemView);
            tv_day = itemView.findViewById(R.id.tv_day_class_header);
        }
    }

    public class ClassHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_class_name;
        TextView tv_class_time;
        TextView tv_class_location;

        ClassHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tv_class_name = itemView.findViewById(R.id.tv_class_name);
            tv_class_time = itemView.findViewById(R.id.tv_class_time);
            tv_class_location = itemView.findViewById(R.id.tv_class_location);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                int position = getAdapterPosition();
                clickListener.onClassClicked(view, position - 1, getItem(position - 1));
            }
        }
    }
}