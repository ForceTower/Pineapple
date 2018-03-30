package com.forcetower.uefs.view.connected.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 29/03/2018.
 */
@SuppressWarnings("WeakerAccess")
public class NewDayClassAdapter extends RecyclerView.Adapter {
    private static final int HEADER = 0, TIME = 1, CLASS = 2, NOTHING = 3;
    private final List<NewScheduleAdapter.InnerLocation> locations;

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

    static class HeaderHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_header)
        TextView header;

        HeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(NewScheduleAdapter.InnerLocation location) {
            header.setText(location.day);
        }
    }

    static class TimeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_start)
        TextView start;
        @BindView(R.id.tv_end)
        TextView end;

        TimeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(NewScheduleAdapter.InnerLocation location) {
            start.setText(location.time.start);
            end.setText(location.time.end);
        }
    }

    static class ClassHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_code)
        TextView classCode;
        @BindView(R.id.tv_group)
        TextView classGroup;

        ClassHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(NewScheduleAdapter.InnerLocation location) {
            classCode.setText(location.location.getClassCode());
            classGroup.setText(location.location.getClassGroup());
        }
    }

    static class NothingHolder extends RecyclerView.ViewHolder {
        NothingHolder(View itemView) {
            super(itemView);
        }
    }
}
