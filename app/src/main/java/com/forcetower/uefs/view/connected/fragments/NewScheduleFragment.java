package com.forcetower.uefs.view.connected.fragments;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentScheduleNewBinding;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.feature.siecomp.SiecompActivity;
import com.forcetower.uefs.game.g2048.activity.Game2048Activity;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.LocationClickListener;
import com.forcetower.uefs.view.connected.adapters.NewScheduleAdapter;
import com.forcetower.uefs.view.connected.adapters.ScheduleAdapter;
import com.forcetower.uefs.vm.base.ScheduleViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by João Paulo on 29/03/2018.
 */
public class NewScheduleFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    AppExecutors executors;

    private ActivityController controller;
    private ScheduleViewModel scheduleViewModel;

    private NewScheduleAdapter scheduleAdapter;
    private ScheduleAdapter subtitleAdapter;
    private FragmentScheduleNewBinding binding;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule_new, container, false);
        controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_schedule);
        binding.btnSiecompSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SiecompActivity.class);
            startActivity(intent);
        });
        setupRecycler();
        setupSubtitles();
        return binding.getRoot();
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
        scheduleAdapter.setOnLongClickListener(long2048Click);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerView.setAdapter(scheduleAdapter);
        binding.recyclerView.setNestedScrollingEnabled(false);
    }

    private void setupSubtitles() {
        subtitleAdapter = new ScheduleAdapter(getContext(), new ArrayList<>(), true);
        subtitleAdapter.setOnClickListener(locationClickListener);
        binding.rvScheduleSubtitle.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvScheduleSubtitle.setAdapter(subtitleAdapter);
        binding.rvScheduleSubtitle.setNestedScrollingEnabled(false);
    }

    private void onReceiveLocations(List<DisciplineClassLocation> locations) {
        if (locations == null || locations.isEmpty()) {
            AnimUtils.fadeOut(getContext(), binding.svSchedule);
            AnimUtils.fadeIn(getContext(), binding.vgNoSchedule);
        } else {
            AnimUtils.fadeOut(getContext(), binding.vgNoSchedule);
            binding.vgNoSchedule.setVisibility(View.GONE);
            binding.svSchedule.setVisibility(View.VISIBLE);
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

    private LocationLongClickListener long2048Click = () -> {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        if (preferences.getBoolean("show_current_semester", true) && preferences.getBoolean("show_score", false)) {
            Game2048Activity.startActivity(requireActivity());
        }
    };

    public interface LocationLongClickListener {
        void onViewLongClicked();
    }
}
