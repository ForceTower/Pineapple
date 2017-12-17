package com.forcetower.uefs.adapters.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.fragments.DisciplinesFragment;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 17/12/2017.
 */

public class SemesterDisciplinesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ClassDetailsView> classesGroups;
    private Context context;
    private DisciplinesFragment.OnDisciplineClickListener onClassClicked;

    public SemesterDisciplinesAdapter(Context context, List<SagresClassDetails> classDetails) {
        this.context = context;
        fillList(classDetails);
    }

    private void fillList(List<SagresClassDetails> classes) {
        classesGroups = new ArrayList<>();

        if (classes == null || classes.isEmpty())
            return;

        for (SagresClassDetails details : classes) {
            for (SagresClassGroup group : details.getGroups()) {
                classesGroups.add(new ClassDetailsView(details, group));
            }
        }

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.class_semester_item, parent, false);
        return new DisciplineHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DisciplineHolder view = (DisciplineHolder) holder;
        ClassDetailsView item = classesGroups.get(position);

        String additionalInfo = context.getString(R.string.class_additional_info_format,
                item.getDetails().getCode(),
                item.getGroup().isDraft() ? item.getDetails().getCredits() : item.getGroup().getCredits(),
                item.getDetails().getMissedClasses()
        );

        String type = item.getGroup().getType();
        view.className.setText(context.getString(R.string.class_name_list_format,
                item.getDetails().getName(),
                (type != null && !type.trim().isEmpty() ? type : ""))
        );

        view.additionalInfo.setText(additionalInfo);

        if (item.getGroup().isDraft()) view.draftImage.setVisibility(View.VISIBLE);
        else view.draftImage.setVisibility(View.INVISIBLE);
    }

    public ClassDetailsView getItem(int position) {
        if (position < 0 || position >= classesGroups.size())
            return null;
        return classesGroups.get(position);
    }

    @Override
    public int getItemCount() {
        return classesGroups.size();
    }

    public void setOnClassClicked(DisciplinesFragment.OnDisciplineClickListener onClassClicked) {
        this.onClassClicked = onClassClicked;
    }

    class ClassDetailsView {
        private SagresClassDetails details;
        private SagresClassGroup group;

        ClassDetailsView(SagresClassDetails details, SagresClassGroup group) {
            this.details = details;
            this.group = group;
        }


        public SagresClassGroup getGroup() {
            return group;
        }

        public SagresClassDetails getDetails() {
            return details;
        }
    }

    class DisciplineHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView className;
        TextView additionalInfo;
        ImageView draftImage;

        DisciplineHolder(View itemView) {
            super(itemView);
            className = itemView.findViewById(R.id.tv_class_name);
            additionalInfo = itemView.findViewById(R.id.tv_additional_info);
            draftImage = itemView.findViewById(R.id.iv_draft_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            ClassDetailsView detailsView = getItem(pos);
            if (onClassClicked != null) {
                onClassClicked.onDisciplineClick(detailsView.getDetails(), detailsView.getGroup());
            }
        }
    }
}
