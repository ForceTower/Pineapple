package com.forcetower.uefs.view.connected.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Grade;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.GradeSection;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class GradesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADER = 1;
    private static final int ITEM = 2;
    private static final int PARTIAL_MEAN = 3;

    private List<GradeView> created = new ArrayList<>();

    public GradesAdapter(List<GradeSection> sections, Grade grade) {
        created = new ArrayList<>();
        setupItems(sections, grade);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade_header, parent, false);
            return new HeaderHolder(view);
        } else if (viewType == PARTIAL_MEAN) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade_partial_mean, parent, false);
            return new PartialHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade_info, parent, false);
            return new GradeHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == HEADER) {
            onBindHeaderHolder((HeaderHolder)holder, position);
        } else if(getItemViewType(position) == PARTIAL_MEAN) {
            onBindPartialHolder((PartialHolder)holder, position);
        } else {
            onBindGradeHolder((GradeHolder)holder, position);
        }
    }

    public void setItems(List<GradeSection> sections, Grade grade) {
        setupItems(sections, grade);
        notifyDataSetChanged();
    }

    private void onBindPartialHolder(PartialHolder holder, int position) {
        GradeView grade = created.get(position);
        holder.tv_partial_mean.setText(grade.headerName);
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
    public int getItemCount() {
        return created.size();
    }

    private void setupItems(List<GradeSection> sections, Grade grade) {
        created.clear();
        if (sections == null || sections.isEmpty()) {
            created.add(new GradeView("Sem notas divulgadas"));
        } else {
            for (GradeSection section : sections) {
                if (section.getName().equalsIgnoreCase("Notas complementares") && grade.getPartialMean() != null) {
                    created.add(new GradeView(true, grade.getPartialMean()));
                }

                created.add(new GradeView(section.getName()));

                if (section.getGrades().isEmpty()) {
                    created.add(new GradeView("Sem notas divulgadas"));
                }

                for (GradeInfo info : section.getGrades()) {
                    created.add(new GradeView(info.getGrade().trim().isEmpty() ? "0,0" : info.getGrade(), info.getDate(), info.getEvaluationName()));
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (created.get(position).header)
            return HEADER;
        if(created.get(position).partialMean)
            return PARTIAL_MEAN;
        return ITEM;
    }

    private class GradeView {
        boolean header;
        boolean partialMean;
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

        GradeView(boolean b, String partial) {
            partialMean = true;
            headerName = partial;
        }
    }

    class GradeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_grade_identification)
        TextView tv_eval_name;
        @BindView(R.id.tv_eval_date)
        TextView tv_date;
        @BindView(R.id.tv_grade)
        TextView tv_grade;

        GradeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class HeaderHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @BindView(R.id.tv_grade_info_name)
        TextView tv_section_name;

        HeaderHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class PartialHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        @BindView(R.id.tv_partial_mean)
        TextView tv_partial_mean;

        PartialHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
