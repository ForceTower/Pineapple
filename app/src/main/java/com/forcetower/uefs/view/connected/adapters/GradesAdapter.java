package com.forcetower.uefs.view.connected.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ItemGradeHeaderBinding;
import com.forcetower.uefs.databinding.ItemGradeInfoBinding;
import com.forcetower.uefs.databinding.ItemGradePartialMeanBinding;
import com.forcetower.uefs.db.entity.Grade;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.GradeSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class GradesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADER = 1;
    private static final int ITEM = 2;
    private static final int PARTIAL_MEAN = 3;

    private List<GradeView> created;

    public GradesAdapter() {
        created = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER) {
            ItemGradeHeaderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_grade_header, parent, false);
            return new HeaderHolder(binding);
        } else if (viewType == PARTIAL_MEAN) {
            ItemGradePartialMeanBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_grade_partial_mean, parent, false);
            return new PartialHolder(binding);
        } else {
            ItemGradeInfoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_grade_info, parent, false);
            return new GradeHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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
        holder.binding.tvPartialMean.setText(grade.headerName);
    }

    private void onBindHeaderHolder(HeaderHolder holder, int position) {
        GradeView grade = created.get(position);
        holder.binding.tvGradeInfoName.setText(grade.headerName);
    }

    private void onBindGradeHolder(GradeHolder holder, int position) {
        GradeView grade = created.get(position);

        holder.binding.tvEvalDate.setText(grade.date);
        holder.binding.tvGradeIdentification.setText(grade.identification);
        holder.binding.tvGrade.setText(grade.grade);
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
        private final ItemGradeInfoBinding binding;

        GradeHolder(ItemGradeInfoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class HeaderHolder extends RecyclerView.ViewHolder {
        private final ItemGradeHeaderBinding binding;

        HeaderHolder(ItemGradeHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    class PartialHolder extends RecyclerView.ViewHolder {
        private final ItemGradePartialMeanBinding binding;

        PartialHolder(ItemGradePartialMeanBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
