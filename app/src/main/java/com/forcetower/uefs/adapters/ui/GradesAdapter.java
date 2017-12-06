package com.forcetower.uefs.adapters.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.sagres_sdk.domain.GradeInfo;
import com.forcetower.uefs.sagres_sdk.domain.GradeSection;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 25/11/2017.
 */

public class GradesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADER = 1;
    private static final int ITEM = 2;

    private Context context;
    private SagresGrade grades;
    private List<GradeView> created = new ArrayList<>();

    public GradesAdapter(Context context, SagresGrade grades) {
        this.context = context;
        this.grades = grades;
        setupGradesList(grades);
    }

    private void setupGradesList(SagresGrade grades) {
        created = new ArrayList<>();

        if (grades == null) {
            created.add(new GradeView("Sem notas divulgadas"));
            created.add(new GradeView("Você trancou essa matéria?"));
            created.add(new GradeView("Isso pode ser um erro! Me avise :)"));
            return;
        }

        if (grades.getSections() == null || grades.getSections().isEmpty()) {
            created.add(new GradeView("Sem notas divulgadas"));
        } else {

            for (GradeSection section : grades.getSections()) {
                created.add(new GradeView(section.getName()));

                if (section.getGrades().isEmpty()) {
                    created.add(new GradeView("Sem notas divulgadas"));
                }

                for (GradeInfo info : section.getGrades()) {
                    created.add(new GradeView(info.getGrade(), info.getDate(), info.getEvaluationName()));
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_grade_header, parent, false);
            return new HeaderHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_grade_item, parent, false);
            return new GradeHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == HEADER) {
            onBindHeaderHolder((HeaderHolder)holder, position);
        } else {
            onBindGradeHolder((GradeHolder)holder, position);
        }
    }

    private void onBindHeaderHolder(HeaderHolder holder, int position) {
        GradeView grade = created.get(position);
        holder.tv_section_name.setText(grade.headerName);
    }

    private void onBindGradeHolder(GradeHolder holder, int position) {
        GradeView grade = created.get(position);
        holder.tv_date.setText(grade.date);
        holder.tv_eval_name.setText(grade.identification);
        holder.tv_grade.setText(grade.grade);
    }

    @Override
    public int getItemViewType(int position) {
        if (created.get(position).header)
            return HEADER;
        return ITEM;
    }


    @Override
    public int getItemCount() {
        return created.size();
    }

    private class GradeView {
        boolean header;
        //Header
        String headerName;
        //Not a header
        String grade;
        String date;
        String identification;

        private GradeView(String grade, String date, String identification){
            this.date = date;
            this.grade = grade;
            this.identification = identification;
            header = false;
        }

        private GradeView(String headerName) {
            this.headerName = headerName;
            header = true;
        }
    }

    class GradeHolder extends RecyclerView.ViewHolder {
        TextView tv_eval_name;
        TextView tv_date;
        TextView tv_grade;

        GradeHolder(View itemView) {
            super(itemView);
            tv_eval_name = itemView.findViewById(R.id.tv_grade_identification);
            tv_date = itemView.findViewById(R.id.tv_eval_date);
            tv_grade = itemView.findViewById(R.id.tv_grade);
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        TextView tv_section_name;

        HeaderHolder(View itemView) {
            super(itemView);
            tv_section_name = itemView.findViewById(R.id.tv_grade_info_name);
        }
    }
}
