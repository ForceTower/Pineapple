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
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.view.connected.DisciplineClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class SemesterAdapter extends RecyclerView.Adapter<SemesterAdapter.SemesterHolder> {
    private Context context;
    private List<Discipline> disciplines;
    private List<Pair<String, List<Discipline>>> mapped;

    private RecyclerView.RecycledViewPool viewPool;
    private DisciplineClickListener clickListener;

    public SemesterAdapter(Context context, List<Discipline> disciplines) {
        this.context = context;
        this.disciplines = disciplines;
        viewPool = new RecyclerView.RecycledViewPool();
        mapped = new ArrayList<>();
        createMap();
    }

    @NonNull
    @Override
    public SemesterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disciplines_semester, parent, false);
        SemesterHolder holder = new SemesterHolder(view);
        holder.recyclerView.setRecycledViewPool(viewPool);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SemesterHolder holder, int position) {
        holder.bind(mapped.get(position));
    }

    @Override
    public int getItemCount() {
        return mapped.size();
    }

    private void createMap() {
        mapped.clear();

        Hashtable<String, List<Discipline>> mapping = new Hashtable<>();
        for (Discipline discipline : disciplines) {
            String semester = discipline.getSemester();
            List<Discipline> classes = mapping.get(semester);
            if (classes == null) classes = new ArrayList<>();
            classes.add(discipline);
            mapping.put(semester, classes);
        }

        Set<String> set = mapping.keySet();
        List<String> semesters = new ArrayList<>();
        semesters.addAll(set);
        Collections.sort(semesters, (o1, o2) -> {
            try {
                int str1 = Integer.parseInt(o1.substring(0, 5));
                int str2 = Integer.parseInt(o2.substring(0, 5));

                if (str1 == str2) {
                    if (o1.length() > 5) return -1;
                    return 1;
                } else {
                    return Integer.compare(str1, str2) * -1;
                }
            } catch (Exception e) {
                return 0;
            }
        });

        for (String semester : semesters) {
            List<Discipline> classes = mapping.get(semester);
            mapped.add(new Pair<>(semester.trim(), classes));
        }
    }

    public void setDisciplines(List<Discipline> disciplines) {
        this.disciplines.clear();
        this.disciplines.addAll(disciplines);
        createMap();
        notifyDataSetChanged();
    }

    public void setClickListener(DisciplineClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class SemesterHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_semester_name)
        TextView tvSemesterName;
        @BindView(R.id.recycler_view)
        RecyclerView recyclerView;
        DisciplinesAdapter adapter;

        SemesterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            adapter = new DisciplinesAdapter(context, new ArrayList<>());
            adapter.setClickListener(clickListener);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }

        public void bind(Pair<String, List<Discipline>> disciplines) {
            tvSemesterName.setText(disciplines.first);
            adapter.setDisciplines(disciplines.second);

        }
    }
}
