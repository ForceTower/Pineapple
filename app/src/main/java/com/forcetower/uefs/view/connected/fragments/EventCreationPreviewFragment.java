package com.forcetower.uefs.view.connected.fragments;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentEventCreationPreviewBinding;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.service.EventsViewModel;
import com.forcetower.uefs.work.event.CreateEventWorker;

import javax.inject.Inject;

/**
 * Created by João Paulo on 16/06/2018.
 */
public class EventCreationPreviewFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;
    @Inject
    NavigationController controller;

    private FragmentEventCreationPreviewBinding binding;
    private EventsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_creation_preview, container, false);
        binding.btnContinue.setOnClickListener(v -> onCreateEventClick());
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(EventsViewModel.class);
        binding.includeEvent.setEvent(viewModel.getCurrentEvent());
    }

    private void onCreateEventClick() {
        CreateEventWorker.invokeWorker(viewModel.getCurrentEvent());
        viewModel.setCurrentEvent(null);
        controller.backTo("list_events");
    }
}
