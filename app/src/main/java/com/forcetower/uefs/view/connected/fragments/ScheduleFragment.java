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
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.view.connected.adapters.ScheduleAdapter;
import com.forcetower.uefs.vm.ScheduleViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by João Paulo on 07/03/2018.
 */
public class ScheduleFragment extends Fragment implements Injectable {
    @BindView(R.id.vg_no_schedule)
    ViewGroup vgNoSchedule;
    @BindView(R.id.recycler_view)
    RecyclerView rvSchedule;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ScheduleAdapter scheduleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, view);
        initializeRecyclerView();
        setupRecyclerView(null);
        return view;
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
            AnimUtils.fadeOut(getContext(), rvSchedule);
            AnimUtils.fadeIn(getContext(), vgNoSchedule);
        } else {
            AnimUtils.fadeOut(getContext(), vgNoSchedule);
            vgNoSchedule.setVisibility(View.GONE);
            rvSchedule.setVisibility(View.VISIBLE);
            setupRecyclerView(locations);
        }
    }

    private void initializeRecyclerView() {
        scheduleAdapter = new ScheduleAdapter(getContext(), new ArrayList<>(), false);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSchedule.setAdapter(scheduleAdapter);
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
