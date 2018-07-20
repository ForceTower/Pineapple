package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import com.forcetower.uefs.databinding.FragmentScheduleBinding;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.adapters.ScheduleAdapter;
import com.forcetower.uefs.vm.base.ScheduleViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by João Paulo on 07/03/2018.
 */
public class ScheduleFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ScheduleAdapter scheduleAdapter;

    private ActivityController controller;
    private FragmentScheduleBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false);
        controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_schedule);
        initializeRecyclerView();
        setupRecyclerView(null);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ScheduleViewModel scheduleViewModel = ViewModelProviders.of(this, viewModelFactory).get(ScheduleViewModel.class);
        scheduleViewModel.getSchedule(null).observe(this, this::onReceiveSchedule);
    }

    private void onReceiveSchedule(List<DisciplineClassLocation> disciplineClassLocations) {
        setupView(disciplineClassLocations);
    }

    private void setupView(List<DisciplineClassLocation> locations) {
        if (locations.isEmpty()) {
            AnimUtils.fadeOut(getContext(), binding.recyclerView);
            AnimUtils.fadeIn(getContext(), binding.vgNoSchedule);
        } else {
            AnimUtils.fadeOut(getContext(), binding.vgNoSchedule);
            binding.vgNoSchedule.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
            setupRecyclerView(locations);
        }
    }

    private void initializeRecyclerView() {
        scheduleAdapter = new ScheduleAdapter(getContext(), new ArrayList<>(), false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(scheduleAdapter);
    }

    private void setupRecyclerView(@Nullable List<DisciplineClassLocation> locations) {
        if (locations == null) {
            Timber.d("Locations are null");
        } else {
            Timber.d("Locations received");
            scheduleAdapter.setLocations(locations);
        }
    }
}
