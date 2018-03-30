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
import com.forcetower.uefs.view.connected.adapters.NewScheduleAdapter;
import com.forcetower.uefs.vm.ScheduleViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 29/03/2018.
 */
public class NewScheduleFragment extends Fragment implements Injectable {
    @BindView(R.id.vg_no_schedule)
    ViewGroup vgNoSchedule;
    @BindView(R.id.recycler_view)
    RecyclerView rvSchedule;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private NewScheduleAdapter scheduleAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_new, container, false);
        ButterKnife.bind(this, view);
        setupRecycler();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ScheduleViewModel scheduleViewModel = ViewModelProviders.of(this, viewModelFactory).get(ScheduleViewModel.class);
        scheduleViewModel.getSchedule(null).observe(this, this::onReceiveLocations);
    }

    private void setupRecycler() {
        scheduleAdapter = new NewScheduleAdapter(getContext(), new ArrayList<>());
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSchedule.setAdapter(scheduleAdapter);
    }

    private void onReceiveLocations(List<DisciplineClassLocation> locations) {
        if (locations == null || locations.isEmpty()) {
            AnimUtils.fadeOut(getContext(), rvSchedule);
            AnimUtils.fadeIn(getContext(), vgNoSchedule);
        } else {
            AnimUtils.fadeOut(getContext(), vgNoSchedule);
            vgNoSchedule.setVisibility(View.GONE);
            rvSchedule.setVisibility(View.VISIBLE);
            scheduleAdapter.setLocations(locations);
        }
    }
}
