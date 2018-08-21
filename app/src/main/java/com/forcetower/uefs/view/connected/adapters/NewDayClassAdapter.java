package com.forcetower.uefs.view.connected.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.view.connected.LocationClickListener;
import com.forcetower.uefs.view.connected.fragments.NewScheduleFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 29/03/2018.
 */
@SuppressWarnings("WeakerAccess")
public class NewDayClassAdapter extends RecyclerView.Adapter {
    private static final int HEADER = 0, TIME = 1, CLASS = 2, NOTHING = 3;
    private final List<NewScheduleAdapter.InnerLocation> locations;
    private LocationClickListener onClickListener;
    private NewScheduleFragment.LocationLongClickListener onLongClickListener;

    public NewDayClassAdapter(List<NewScheduleAdapter.InnerLocation> locations) {
        this.locations = new ArrayList<>();
        setList(locations);
    }

    public void setList(List<NewScheduleAdapter.InnerLocation> list) {
        this.locations.clear();
        this.locations.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == HEADER) {
            view = inflater.inflate(R.layout.item_schedule_header, parent, false);
            return new HeaderHolder(view);
        } else if (viewType == TIME) {
            view = inflater.inflate(R.layout.item_schedule_time, parent, false);
            return new TimeHolder(view);
        } else if (viewType == CLASS) {
            view = inflater.inflate(R.layout.item_schedule_class_new, parent, false);
            return new ClassHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_schedule_empty, parent, false);
            return new NothingHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        NewScheduleAdapter.InnerLocation location = locations.get(position);
        if (type == HEADER) ((HeaderHolder)holder).bind(location);
        if (type == TIME)   ((TimeHolder)  holder).bind(location);
        if (type == CLASS)  ((ClassHolder) holder).bind(location);
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    @Override
    public int getItemViewType(int position) {
        NewScheduleAdapter.InnerLocation location = locations.get(position);
        if (location.header) return HEADER;
        if (location.timeRow) return TIME;
        if (location.hasClass()) return CLASS;
        else return NOTHING;
    }

    public void setOnClickListener(LocationClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(NewScheduleFragment.LocationLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    static class HeaderHolder extends RecyclerView.ViewHolder {
        final TextView header;

        HeaderHolder(View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.tv_header);
        }

        void bind(NewScheduleAdapter.InnerLocation location) {
            header.setText(location.day);
        }
    }

    static class TimeHolder extends RecyclerView.ViewHolder {
        final TextView start;
        final TextView end;

        TimeHolder(View itemView) {
            super(itemView);
            start = itemView.findViewById(R.id.tv_start);
            end = itemView.findViewById(R.id.tv_end);
        }

        void bind(NewScheduleAdapter.InnerLocation location) {
            start.setText(location.time.start);
            end.setText(location.time.end);
        }
    }

    class ClassHolder extends RecyclerView.ViewHolder {
        final LinearLayout container;
        final TextView classCode;
        final TextView classGroup;
        final int colors[];

        ClassHolder(View itemView) {
            super(itemView);
            colors = itemView.getContext().getResources().getIntArray(R.array.discipline_colors);
            itemView.setOnClickListener(v -> onClick());
            itemView.setOnLongClickListener(v -> onLongClick());
            container = itemView.findViewById(R.id.ll_container);
            classCode = itemView.findViewById(R.id.tv_code);
            classGroup = itemView.findViewById(R.id.tv_group);
        }

        private boolean onLongClick() {
            onLongClickListener.onViewLongClicked();
            return true;
        }

        private void onClick() {
            int position = getAdapterPosition();
            NewScheduleAdapter.InnerLocation location = locations.get(position);
            if (location.location != null && onClickListener != null) {
                onClickListener.onDisciplineGroupClicked(location.location);
            }
        }

        void bind(NewScheduleAdapter.InnerLocation location) {
            classCode.setText(location.location.getClassCode());
            classGroup.setText(location.location.getClassGroup());
            container.setBackgroundColor(colors[location.colorIndex % colors.length]);
        }
    }

    static class NothingHolder extends RecyclerView.ViewHolder {
        NothingHolder(View itemView) {
            super(itemView);
        }
    }
}
