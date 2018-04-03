package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.forcetower.uefs.util.DateUtils.getDayOfWeek;
import static com.forcetower.uefs.util.DateUtils.toWeekLongDay;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
    private List<DisciplineClassLocation> locations;
    private List<Pair<String, List<DisciplineClassLocation>>> mapped;
    private RecyclerView.RecycledViewPool viewPool;
    private Context context;

    public ScheduleAdapter(Context context, @NonNull List<DisciplineClassLocation> locations) {
        this.context = context;
        this.locations = locations;
        createMap();
        viewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public ScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_day, parent, false);
        ScheduleViewHolder holder = new ScheduleViewHolder(view);
        holder.innerRecyclerView.setRecycledViewPool(viewPool);
        return holder;
    }

    @Override
    public void onBindViewHolder(ScheduleViewHolder holder, int position) {
        holder.bind(mapped.get(position));
    }

    @Override
    public int getItemCount() {
        return mapped.size();
    }

    public void setLocations(List<DisciplineClassLocation> locations) {
        this.locations.clear();
        this.locations.addAll(locations);
        mapped = new ArrayList<>();
        createMap();
        Timber.d("New map size: %s", mapped.size());
        notifyDataSetChanged();
    }

    private void createMap() {
        Hashtable<String, List<DisciplineClassLocation>> mapping = new Hashtable<>();
        for (DisciplineClassLocation location : locations) {
            String day = location.getDay();

            List<DisciplineClassLocation> classes = mapping.get(day);
            if (classes == null) {
                classes = new ArrayList<>();
                Timber.d("Created list for day %s", day);
            }
            classes.add(location);
            mapping.put(day, classes);
        }

        mapped = new ArrayList<>();
        for (int i = 1; i <= 7; i++) {
            String day = getDayOfWeek(i);
            if (mapping.containsKey(day)) {
                List<DisciplineClassLocation> classes = mapping.get(day);
                Collections.sort(classes);
                mapped.add(new Pair<>(toWeekLongDay(context, day), classes));
            }
        }

    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.inner_recycler_view)
        RecyclerView innerRecyclerView;
        @BindView(R.id.tv_day)
        TextView tvDay;

        DayClassAdapter adapter;

        ScheduleViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            innerRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new DayClassAdapter(context, new ArrayList<>());
            innerRecyclerView.setAdapter(adapter);
        }

        public void bind(Pair<String, List<DisciplineClassLocation>> disciplineClassLocations) {
            adapter.setDisciplines(disciplineClassLocations.second);
            tvDay.setText(disciplineClassLocations.first);
        }
    }
}
