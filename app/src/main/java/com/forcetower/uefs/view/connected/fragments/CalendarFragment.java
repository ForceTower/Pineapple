package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentCalendarBinding;
import com.forcetower.uefs.db.entity.CalendarItem;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.adapters.CalendarAdapter;
import com.forcetower.uefs.vm.base.CalendarViewModel;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by João Paulo on 08/03/2018.
 */

public class CalendarFragment extends Fragment implements Injectable {
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private CalendarAdapter calendarAdapter;
    private CalendarViewModel calendarViewModel;
    private ActivityController controller;

    private FragmentCalendarBinding binding;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);
        controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.title_calendar);
        setupRecycler();
        setupRefreshLayout();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        calendarViewModel = ViewModelProviders.of(this, viewModelFactory).get(CalendarViewModel.class);
        calendarViewModel.getCalendar().observe(this, this::onReceiveCalendar);
        binding.swipeRefresh.setRefreshing(calendarViewModel.isRefreshing());
        calendarViewModel.refreshManual(false).observe(this, this::onUpdateReceived);
    }

    private void onReceiveCalendar(List<CalendarItem> calendarItems) {
        calendarAdapter.submitList(calendarItems);
    }

    private void setupRecycler() {
        calendarAdapter = new CalendarAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        binding.recyclerView.setAdapter(calendarAdapter);
    }

    private void setupRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener(() -> {
            if (calendarViewModel.isRefreshing()) return;

            calendarViewModel.refreshManual(true).observe(this, this::onUpdateReceived);
            binding.swipeRefresh.setRefreshing(true);
            calendarViewModel.setRefreshing(true);
        });
    }

    private void onUpdateReceived(Resource<Integer> resource) {
        if (resource == null) return;

        if (resource.status == Status.SUCCESS) {
            binding.swipeRefresh.setRefreshing(false);
            calendarViewModel.setRefreshing(false);
        } else if (resource.status == Status.ERROR) {
            binding.swipeRefresh.setRefreshing(false);
            calendarViewModel.setRefreshing(false);
            if (resource.data != null)
                Toast.makeText(getContext(), resource.data, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), resource.message, Toast.LENGTH_SHORT).show();
        } else {
            //noinspection ConstantConditions
            Timber.d("Updating.. Received Status: %s", getString(resource.data));
        }
    }
}
