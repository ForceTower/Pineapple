package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.view.connected.LocationClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.forcetower.uefs.util.DateUtils.getDayOfWeek;

/**
 * Created by João Paulo on 29/03/2018.
 */
public class NewScheduleAdapter extends RecyclerView.Adapter<NewScheduleAdapter.ScheduleHolder> {
    private final List<DisciplineClassLocation> locations;
    private final RecyclerView.RecycledViewPool viewPool;
    private final List<List<InnerLocation>> mapped;
    private LocationClickListener onClickListener;

    public NewScheduleAdapter(@NonNull Context context, @NonNull List<DisciplineClassLocation> locations) {
        this.locations = new ArrayList<>();
        this.viewPool = new RecyclerView.RecycledViewPool();
        this.mapped = new ArrayList<>();

        setLocations(locations);
    }

    @NonNull
    @Override
    public ScheduleHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_schedule_day_new, parent, false);
        ScheduleHolder holder = new ScheduleHolder(view);
        holder.innerRecycler.setRecycledViewPool(viewPool);
        holder.innerRecycler.setNestedScrollingEnabled(false);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleHolder holder, int position) {
        holder.bind(mapped.get(position));
    }

    @Override
    public int getItemCount() {
        return mapped.size();
    }

    public void setLocations(List<DisciplineClassLocation> locations) {
        this.locations.clear();
        this.locations.addAll(locations);

        createMap();
        notifyDataSetChanged();
    }

    private void createMap() {
        Hashtable<String, List<InnerLocation>> mapping = new Hashtable<>();
        List<ClassTime> times = new ArrayList<>();
        HashMap<String, Integer> codesColors = new HashMap<>();
        int indexer = 0;

        for (DisciplineClassLocation location : locations) {
            //mapping default
            String day = location.getDay();

            List<InnerLocation> classes = mapping.get(day);
            if (classes == null) {
                classes = new ArrayList<>();
            }

            //times default
            String start = location.getStartTime();
            String end = location.getEndTime();
            ClassTime time = new ClassTime(start, end);

            //codes default
            if (!codesColors.containsKey(location.getClassCode()))
                codesColors.put(location.getClassCode(), indexer++);

            //add stuff
            int color = codesColors.get(location.getClassCode());
            classes.add(new InnerLocation(location, time, color));
            mapping.put(day, classes);
            if (!times.contains(time)) times.add(time);
        }

        mapped.clear();
        //Adds the first line
        if (times.isEmpty()) return;

        Collections.sort(times);

        List<InnerLocation> line = new ArrayList<>();
        //Add a empty box
        line.add(new InnerLocation(""));
        for (ClassTime time : times) {
            line.add(new InnerLocation(time));
        }
        mapped.add(line);

        for (int i = 1; i <= 7; i++) {
            String day = getDayOfWeek(i);
            if (mapping.containsKey(day)) {
                List<InnerLocation> classes = mapping.get(day);
                Collections.sort(classes);

                List<InnerLocation> fullDay = new ArrayList<>();
                //Adds the header
                //Collections.sort(fullDay);
                fullDay.add(new InnerLocation(day));

                for (ClassTime time : times) {
                    //noinspection SuspiciousMethodCalls
                    int index = classes.indexOf(time);
                    if (index == -1) {
                        fullDay.add(new InnerLocation(null, time, 0));
                    } else {

                        fullDay.add(classes.get(index));
                    }
                }
                mapped.add(fullDay);
            }
        }
    }

    public void setOnClickListener(LocationClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    class ScheduleHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recycler_view)
        RecyclerView innerRecycler;

        NewDayClassAdapter adapter;

        ScheduleHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            innerRecycler.setLayoutManager(new LinearLayoutManager(itemView.getContext()) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }

                @Override
                public boolean canScrollHorizontally() {
                    return false;
                }
            });
            innerRecycler.setNestedScrollingEnabled(false);

            adapter = new NewDayClassAdapter(new ArrayList<>());
            adapter.setOnClickListener(onClickListener);
            innerRecycler.setAdapter(adapter);
        }

        void bind(List<InnerLocation> locations) {
            adapter.setList(locations);
        }
    }

    static class InnerLocation implements Comparable<InnerLocation> {
        DisciplineClassLocation location;
        ClassTime time;
        String day;
        int colorIndex;
        boolean header = false;
        boolean timeRow = false;
        boolean nothing = false;

        InnerLocation(DisciplineClassLocation location, ClassTime time, int colorIndex) {
            this.location = location;
            this.time = time;
            this.colorIndex = colorIndex;
        }

        InnerLocation(String header) {
            this.day = header;
            this.header = true;
        }

        InnerLocation(ClassTime time) {
            this.time = time;
            this.timeRow = true;
        }

        InnerLocation() {
            nothing = true;
        }

        public boolean hasClass() {
            return location != null;
        }

        @Override
        public int compareTo(@NonNull InnerLocation o) {
            if (header) return -1;
            return time.compareTo(o.time);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof InnerLocation) {
                InnerLocation o = (InnerLocation) obj;
                return o.location.getUid() == location.getUid();
            }

            if (obj instanceof ClassTime) {
                ClassTime o = (ClassTime) obj;
                return o.equals(time);
            }

            return false;
        }

        @Override
        public String toString() {
            return "Time: " + time + " -- " + (location == null ? "free time" : location.getClassName());
        }
    }

    static class ClassTime implements Comparable<ClassTime> {
        final String start;
        final String end;

        ClassTime(String start, String end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ClassTime) {
                ClassTime o = (ClassTime) obj;
                return o.start.equalsIgnoreCase(start) && o.end.equalsIgnoreCase(end);
            }

            if (obj instanceof InnerLocation) {
                InnerLocation o = (InnerLocation) obj;
                return o.time.equals(this);
            }
            return false;
        }

        @Override
        public String toString() {
            return "{" + start + " -> " + end + "}";
        }

        @Override
        public int compareTo(@NonNull ClassTime o) {
            return start.compareTo(o.start);
        }
    }
}
