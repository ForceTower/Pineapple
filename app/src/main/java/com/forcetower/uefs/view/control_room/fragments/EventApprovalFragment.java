package com.forcetower.uefs.view.control_room.fragments;

import android.arch.lifecycle.ViewModelProviders;
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
import com.forcetower.uefs.databinding.FragmentEventApprovalBinding;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.view.connected.adapters.EventAdapter;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.service.EventsViewModel;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by João Paulo on 21/06/2018.
 */
public class EventApprovalFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;

    private EventAdapter adapter;
    private FragmentEventApprovalBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_approval, container, false);
        prepareRecyclerView();
        return binding.getRoot();
    }

    private void prepareRecyclerView() {
        adapter = new EventAdapter();
        binding.rvEvents.setNestedScrollingEnabled(false);
        binding.rvEvents.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvEvents.setAdapter(adapter);
        binding.rvEvents.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventsViewModel eventsViewModel = ViewModelProviders.of(this, viewModelFactory).get(EventsViewModel.class);
        eventsViewModel.getUnapprovedEvents().observe(this, this::onEventsUpdate);
    }

    private void onEventsUpdate(Resource<List<Event>> eventsRes) {
        if (eventsRes.data != null) {
            if (eventsRes.data.isEmpty()) {
                binding.rvEvents.setVisibility(View.GONE);
                binding.textNoEvents.setVisibility(View.VISIBLE);
            } else {
                adapter.setEvents(eventsRes.data);
                binding.rvEvents.setVisibility(View.VISIBLE);
                binding.textNoEvents.setVisibility(View.GONE);
            }
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
