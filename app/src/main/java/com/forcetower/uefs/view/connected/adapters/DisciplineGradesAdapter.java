package com.forcetower.uefs.view.connected.adapters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Discipline;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class DisciplineGradesAdapter extends RecyclerView.Adapter<DisciplineGradesAdapter.DisciplineHolder> {
    private final Context context;
    private final List<Discipline> disciplines;
    private final RecyclerView.RecycledViewPool viewPool;

    public DisciplineGradesAdapter(Context context, List<Discipline> disciplines) {
        this.context = context;
        this.disciplines = disciplines;
        this.viewPool = new RecyclerView.RecycledViewPool();
    }

    @Override
    public DisciplineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade_discipline, parent, false);
        DisciplineHolder holder = new DisciplineHolder(view);
        holder.recyclerView.setRecycledViewPool(viewPool);
        return holder;
    }

    @Override
    public void onBindViewHolder(DisciplineHolder holder, int position) {
        holder.bind(disciplines.get(position));
    }

    @Override
    public int getItemCount() {
        return disciplines.size();
    }

    public void setDisciplines(List<Discipline> disciplines) {
        this.disciplines.clear();
        this.disciplines.addAll(disciplines);
        notifyDataSetChanged();
    }

    public class DisciplineHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recycler_view)
        RecyclerView recyclerView;
        @BindView(R.id.tv_class_name)
        TextView tvClassName;
        @BindView(R.id.mean_text)
        TextView tvMeanText;

        DisciplineHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Discipline discipline) {
            tvClassName.setText(discipline.getName());
            if (discipline.getGrade() != null) {
                tvMeanText.setText(discipline.getGrade().getFinalScore());
            } else {
                tvMeanText.setText("--");
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new GradesAdapter(discipline.getSections(), discipline.getGrade()));
        }
    }
}
