package com.forcetower.uefs.adapters.ui;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.fragments.DisciplinesFragment;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 17/12/2017.
 */

public class AllDisciplinesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> auxiliary;
    private List<List<SagresClassDetails>> classesPerSemester;
    private Context context;
    private DisciplinesFragment.OnDisciplineClickListener onClassClicked;

    public AllDisciplinesAdapter(Context context, List<SagresClassDetails> detailsList, DisciplinesFragment.OnDisciplineClickListener classClicked) {
        this.context = context;
        setOnClassClicked(classClicked);
        createHashMap(detailsList);
    }

    private void createHashMap(List<SagresClassDetails> detailsList) {
        classesPerSemester = new ArrayList<>();
        auxiliary = new ArrayList<>();

        if (detailsList == null || detailsList.isEmpty()) {
            auxiliary.add("Não encontrado =/");
            classesPerSemester.add(new ArrayList<>());
            notifyDataSetChanged();
            return;
        }

        for (SagresClassDetails details : detailsList) {
            String semester = details.getSemester();
            List<SagresClassDetails> hashList;
            if (auxiliary.contains(semester)) {
                hashList = classesPerSemester.get(auxiliary.indexOf(semester));
                hashList.add(details);
            } else {
                hashList = new ArrayList<>();
                hashList.add(details);
                classesPerSemester.add(hashList);
                auxiliary.add(semester);
            }

        }

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.classes_semester_item, parent, false);
        return new SemesterHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SemesterHolder view = (SemesterHolder) holder;
        view.semesterText.setText(auxiliary.get(position));
        if (Utils.isLollipop()) view.relativeLayout.setElevation(3);

        SemesterDisciplinesAdapter adapter = new SemesterDisciplinesAdapter(context, classesPerSemester.get(position));
        adapter.setOnClassClicked(onClassClicked);
        view.classes.setLayoutManager(new LinearLayoutManager(context));
        view.classes.setAdapter(adapter);
        view.classes.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        view.classes.setNestedScrollingEnabled(false);
    }

    @Override
    public int getItemCount() {
        return classesPerSemester.size();
    }

    public void setOnClassClicked(DisciplinesFragment.OnDisciplineClickListener onClassClicked) {
        this.onClassClicked = onClassClicked;
    }

    class SemesterHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        TextView semesterText;
        RecyclerView classes;

        SemesterHolder(View itemView) {
            super(itemView);
            semesterText = itemView.findViewById(R.id.tv_semester);
            classes = itemView.findViewById(R.id.rv_semester_disciplines);
            relativeLayout = itemView.findViewById(R.id.rl_layout_root);
        }
    }
}
