package com.forcetower.uefs.view.connected.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentEventCreationOneBinding;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.service.EventsViewModel;

import javax.inject.Inject;

/**
 * Created by João Paulo on 16/06/2018.
 */
public class EventCreationOneFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;
    @Inject
    NavigationController controller;

    private FragmentEventCreationOneBinding binding;
    private EventsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_creation_one, container, false);
        binding.btnContinue.setOnClickListener(v -> onNextEvent());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(EventsViewModel.class);
    }

    private void onNextEvent() {
        if (validFormData())
            controller.navigateToCreateEventTwo();
    }

    private boolean validFormData() {
        boolean valid = true;

        String name = binding.eventTitle.getText().toString();
        String desc = binding.eventDescription.getText().toString();

        if (desc.trim().length() < 4) {
            valid = false;
            binding.eventDescription.setError(getString(R.string.event_short_description_too_short));
            binding.eventDescription.requestFocus();
        }

        if (name.trim().length() < 4) {
            valid = false;
            binding.eventTitle.setError(getString(R.string.event_title_too_short));
            binding.eventTitle.requestFocus();
        }

        return valid;
    }

    public void setupInterface(Event event) {
        binding.eventDescription.setText(event.getSubtitle());
        binding.eventTitle.setText(event.getName());
    }

    @Override
    public void onResume() {
        super.onResume();
        Event current = viewModel.getCurrentEvent();
        setupInterface(current);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveFormData();
    }

    private void saveFormData() {
        viewModel.getCurrentEvent().setName(binding.eventTitle.getText().toString().trim());
        viewModel.getCurrentEvent().setSubtitle(binding.eventDescription.getText().toString().trim());
    }
}
