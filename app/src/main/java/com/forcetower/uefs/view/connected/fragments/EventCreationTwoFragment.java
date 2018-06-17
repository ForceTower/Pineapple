package com.forcetower.uefs.view.connected.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentEventCreationTwoBinding;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.service.EventsViewModel;

import java.util.Calendar;

import javax.inject.Inject;

import static com.forcetower.uefs.util.SupportUtils.getGravityCompat;
import static com.forcetower.uefs.util.WordUtils.stringify;

/**
 * Created by João Paulo on 16/06/2018.
 */
public class EventCreationTwoFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;
    @Inject
    NavigationController controller;

    private FragmentEventCreationTwoBinding binding;
    private EventsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_creation_two, container, false);
        binding.btnContinue.setOnClickListener(v -> onNextClick());
        binding.eventStartDate.setOnClickListener(v -> selectDate());
        binding.eventStartTime.setOnClickListener(v -> selectTime());
        return binding.getRoot();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory).get(EventsViewModel.class);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveFormData();
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareInterface(viewModel.getCurrentEvent());
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int mont = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);


        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, sYear, sMonth, sDay) -> {
            sMonth++;
            binding.eventStartDate.setText(getString(R.string.date_format, stringify(sDay), stringify(sMonth), sYear));
            binding.eventStartDate.clearFocus();
        }, year, mont, date);
        datePickerDialog.setTitle(R.string.event_date_start_dialog);
        datePickerDialog.show();
    }

    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        int hour   = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), ((view, sHourOfDay, sMinute) -> {
            binding.eventStartTime.setText(getString(R.string.time_format, stringify(sHourOfDay), stringify(sMinute)));
            binding.eventStartTime.clearFocus();
        }), hour, minute, true);
        timePickerDialog.setTitle(R.string.event_time_start_dialog);
        timePickerDialog.show();
    }

    private void saveFormData() {
        viewModel.getCurrentEvent().setLocation(binding.eventLocation.getText().toString().trim());
        viewModel.getCurrentEvent().setStartDate(binding.eventStartDate.getText().toString().trim());
        viewModel.getCurrentEvent().setStartTime(binding.eventStartTime.getText().toString().trim());
    }

    private void prepareInterface(Event event) {
        binding.eventLocation.setText(event.getLocation());
        binding.eventStartDate.setText(event.getStartDate());
        binding.eventStartTime.setText(event.getStartTime());
    }

    private void onNextClick() {
        if (validFormData()) {
            if (VersionUtils.isLollipop()) {
                setExitTransition(new Slide(getGravityCompat(requireContext(), Gravity.START)));
                setAllowEnterTransitionOverlap(false);
                setAllowReturnTransitionOverlap(false);
            }
            controller.navigateToCreateEventThree(requireContext());
        }
    }

    private boolean validFormData() {
        boolean valid = true;

        String location = binding.eventLocation.getText().toString().trim();
        String startDate = binding.eventStartDate.getText().toString().trim();
        String startTime = binding.eventStartTime.getText().toString().trim();

        if (location.length() < 4) {
            valid = false;
            binding.eventLocation.setError(getString(R.string.validation_error_format, 4));
            binding.eventLocation.requestFocus();
        }

        if (startDate.length() < 5) {
            valid = false;
            binding.eventStartDate.setError(getString(R.string.validation_error_format, 5));
            binding.eventStartDate.requestFocus();
        }

        if (startTime.length() < 4) {
            valid = false;
            binding.eventStartTime.setError(getString(R.string.validation_error_format, 4));
            binding.eventStartTime.requestFocus();
        }

        return valid;
    }
}
