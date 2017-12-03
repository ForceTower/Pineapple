package com.forcetower.uefs.adapters.ui;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;

import java.util.List;

/**
 * Created by João Paulo on 02/12/2017.
 */

public class AllGradesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<SagresGrade> grades;

    public AllGradesAdapter(Context context, List<SagresGrade> grades) {
        this.grades = grades;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ClassHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_class_grade_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ClassHolder classHolder = (ClassHolder) holder;
        SagresGrade current = grades.get(position);

        classHolder.className.setText(current.getClassName());
        classHolder.mean_text.setText(current.getFinalScore());

        GradesAdapter gradesAdapter = new GradesAdapter(context, current);
        classHolder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        classHolder.recyclerView.setAdapter(gradesAdapter);
        classHolder.recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        //classHolder.recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public int getItemCount() {
        return grades.size();
    }

    private class ClassHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView className;
        TextView mean_text;

        ClassHolder(View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.tv_class_name);
            mean_text = itemView.findViewById(R.id.mean_text);
            recyclerView = itemView.findViewById(R.id.rv_discipline_grades);
        }
    }
}
