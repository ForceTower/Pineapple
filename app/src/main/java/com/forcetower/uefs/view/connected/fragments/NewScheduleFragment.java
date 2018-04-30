package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.LocationClickListener;
import com.forcetower.uefs.view.connected.adapters.NewScheduleAdapter;
import com.forcetower.uefs.view.connected.adapters.ScheduleAdapter;
import com.forcetower.uefs.vm.base.ScheduleViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by João Paulo on 29/03/2018.
 */
public class NewScheduleFragment extends Fragment implements Injectable {
    @BindView(R.id.vg_no_schedule)
    ViewGroup vgNoSchedule;
    @BindView(R.id.recycler_view)
    RecyclerView rvSchedule;
    @BindView(R.id.rv_schedule_subtitle)
    RecyclerView rvScheduleSubtitle;
    @BindView(R.id.sv_schedule)
    NestedScrollView svSchedule;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    AppExecutors executors;

    private ActivityController controller;
    private ScheduleViewModel scheduleViewModel;

    private NewScheduleAdapter scheduleAdapter;
    private ScheduleAdapter subtitleAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            controller = (ActivityController) context;
        } catch (ClassCastException e) {
            Timber.e("Class %s must implement MainContentController", context.getClass().getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_new, container, false);
        ButterKnife.bind(this, view);
        controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_schedule);
        setupRecycler();
        setupSubtitles();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        scheduleViewModel = ViewModelProviders.of(this, viewModelFactory).get(ScheduleViewModel.class);
        scheduleViewModel.getSchedule(null).observe(this, this::onReceiveLocations);
    }

    private void setupRecycler() {
        scheduleAdapter = new NewScheduleAdapter(requireContext(), new ArrayList<>());
        scheduleAdapter.setOnClickListener(locationClickListener);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSchedule.setAdapter(scheduleAdapter);
        rvSchedule.setNestedScrollingEnabled(false);
    }

    private void setupSubtitles() {
        subtitleAdapter = new ScheduleAdapter(getContext(), new ArrayList<>(), true);
        subtitleAdapter.setOnClickListener(locationClickListener);
        rvScheduleSubtitle.setLayoutManager(new LinearLayoutManager(getContext()));
        rvScheduleSubtitle.setAdapter(subtitleAdapter);
        rvScheduleSubtitle.setNestedScrollingEnabled(false);
    }

    private void onReceiveLocations(List<DisciplineClassLocation> locations) {
        if (locations == null || locations.isEmpty()) {
            AnimUtils.fadeOut(getContext(), svSchedule);
            AnimUtils.fadeIn(getContext(), vgNoSchedule);
        } else {
            AnimUtils.fadeOut(getContext(), vgNoSchedule);
            vgNoSchedule.setVisibility(View.GONE);
            svSchedule.setVisibility(View.VISIBLE);
            try {
                scheduleAdapter.setLocations(locations);
                subtitleAdapter.setLocations(locations);
                Timber.d(locations.get(0).getDay());
            } catch (Exception ex) {
                controller.showNewScheduleError(ex);
            }
        }
    }

    private LocationClickListener locationClickListener = location -> executors.others().execute(() -> {
        int groupId = location.getGroupId();
        DisciplineGroup group = scheduleViewModel.getDisciplineGroupDirect(groupId);
        int disciplineId = group.getDiscipline();
        if (getContext() != null) {
            executors.mainThread().execute(() -> controller.navigateToDisciplineDetails(groupId, disciplineId));
        }
    });
}
