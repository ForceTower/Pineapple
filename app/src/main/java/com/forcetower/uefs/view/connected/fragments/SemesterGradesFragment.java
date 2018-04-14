package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.view.connected.adapters.DisciplineGradesAdapter;
import com.forcetower.uefs.vm.GradesViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by João Paulo on 07/03/2018.
 */
public class SemesterGradesFragment extends Fragment implements Injectable {
    @BindView(R.id.recycler_view)
    RecyclerView rvDisciplines;
    @BindView(R.id.vg_loading)
    ViewGroup vgLoading;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private DisciplineGradesAdapter disciplineGradesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_semester_grades, container, false);
        ButterKnife.bind(this, view);
        setupRecycler();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null && getArguments().getString("semester") != null) {
            String semester = getArguments().getString("semester");
            GradesViewModel gradesViewModel = ViewModelProviders.of(this, viewModelFactory).get(GradesViewModel.class);
            gradesViewModel.getGrades(semester).observe(this, this::onGradesReceived);
        }
    }

    private void onGradesReceived(List<Discipline> disciplines) {
        Timber.d("Received stuff: %s", disciplines);
        if (disciplines != null) {
            AnimUtils.fadeOut(getContext(), vgLoading);
            disciplineGradesAdapter.setDisciplines(disciplines);
        }
    }

    private void setupRecycler() {
        disciplineGradesAdapter = new DisciplineGradesAdapter(getContext(), new ArrayList<>());
        rvDisciplines.setLayoutManager(new LinearLayoutManager(getContext()));
        rvDisciplines.setAdapter(disciplineGradesAdapter);
    }
}
