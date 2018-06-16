package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentEventsBinding;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.view.connected.ActivityController;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.view.connected.adapters.EventAdapter;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.service.EventsViewModel;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by João Paulo on 15/06/2018.
 */
public class EventsFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;
    @Inject
    NavigationController navigation;

    private FragmentEventsBinding binding;
    private EventAdapter adapter;
    private ActivityController controller;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        controller = (ActivityController) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_events, container, false);
        controller.getTabLayout().setVisibility(View.GONE);
        controller.changeTitle(R.string.nav_title_events);

        binding.btnCreateEvent.setOnClickListener(v -> onCreateEvent());
        prepareRecyclerView();
        return binding.getRoot();
    }

    private void onCreateEvent() {
        navigation.navigateToCreateEvent();
    }

    private void prepareRecyclerView() {
        adapter = new EventAdapter();
        binding.recyclerEvents.setNestedScrollingEnabled(false);
        binding.recyclerEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerEvents.setAdapter(adapter);
        binding.recyclerEvents.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventsViewModel eventsViewModel = ViewModelProviders.of(this, viewModelFactory).get(EventsViewModel.class);
        eventsViewModel.getEvents().observe(this, this::onEventsUpdate);
    }

    private void onEventsUpdate(Resource<List<Event>> eventsRes) {
        if (eventsRes.data != null && !eventsRes.data.isEmpty()) {
            adapter.setEvents(eventsRes.data);
        } else {
            binding.recyclerEvents.setVisibility(View.GONE);
            binding.textNoEvents.setVisibility(View.VISIBLE);
        }

        switch (eventsRes.status) {
            case ERROR:
                Timber.d("Error loading from network");
                Timber.d("Code: " + eventsRes.code);
                Timber.d("Message: " + eventsRes.message);
                break;
            case LOADING:
                Timber.d("Loading from network");
                break;
            case SUCCESS:
                Timber.d("Success loading data from network");
                break;
        }
    }
}
