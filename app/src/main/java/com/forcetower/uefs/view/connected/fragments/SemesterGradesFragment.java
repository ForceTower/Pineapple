package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentSemesterGradesBinding;
import com.forcetower.uefs.db.entity.Discipline;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.view.connected.adapters.DisciplineGradesAdapter;
import com.forcetower.uefs.vm.base.GradesViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by João Paulo on 07/03/2018.
 */
public class SemesterGradesFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private DisciplineGradesAdapter disciplineGradesAdapter;
    private FragmentSemesterGradesBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_semester_grades, container, false);
        setupRecycler();
        return binding.getRoot();
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
        if (disciplines != null) {
            AnimUtils.fadeOut(getContext(), binding.vgLoading);
            disciplineGradesAdapter.setDisciplines(disciplines);
        }
    }

    private void setupRecycler() {
        disciplineGradesAdapter = new DisciplineGradesAdapter(getContext(), new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(disciplineGradesAdapter);
    }
}
