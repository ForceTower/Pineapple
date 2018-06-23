package com.forcetower.uefs.view.connected.fragments;

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
import com.forcetower.uefs.databinding.FragmentEventCreationThreeBinding;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.service.EventsViewModel;

import javax.inject.Inject;

import timber.log.Timber;

import static com.forcetower.uefs.util.SupportUtils.getGravityCompat;

/**
 * Created by João Paulo on 16/06/2018.
 */
public class EventCreationThreeFragment extends Fragment implements Injectable {
    @Inject
    UEFSViewModelFactory viewModelFactory;
    @Inject
    NavigationController controller;

    private FragmentEventCreationThreeBinding binding;
    private EventsViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_creation_three, container, false);
        binding.btnContinue.setOnClickListener(v -> onNextEvent());
        binding.cbProvidesHours.setOnCheckedChangeListener((v, val) -> onProvideHoursChange(val));
        binding.cbEventFree.setOnCheckedChangeListener((v, val) -> onFreeChange(val));
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

    private void prepareInterface(Event event) {
        binding.cbEventFree.setChecked(event.isFree());
        binding.eventLongDescription.setText(event.getDescription());
        binding.eventOrganization.setText(event.getOfferedBy());
        binding.eventPrice.setText(getString(R.string.double_format, event.getPrice()));
        binding.cbProvidesHours.setChecked(event.isHasCertificate());
        binding.eventHours.setText(getString(R.string.integer_format, event.getCertificateHours()));
    }

    private void onProvideHoursChange(boolean checked) {
        binding.hoursLayout.setVisibility(checked ? View.VISIBLE : View.GONE);
    }

    private void onFreeChange(boolean checked) {
        binding.priceLayout.setVisibility(checked ? View.GONE : View.VISIBLE);
    }

    private void saveFormData() {
        viewModel.getCurrentEvent().setFree(binding.cbEventFree.isChecked());
        viewModel.getCurrentEvent().setDescription(binding.eventLongDescription.getText().toString().trim());
        viewModel.getCurrentEvent().setOfferedBy(binding.eventOrganization.getText().toString().trim());
        viewModel.getCurrentEvent().setHasCertificate(binding.cbProvidesHours.isChecked());
        try {
            viewModel.getCurrentEvent().setPrice(Double.parseDouble(binding.eventPrice.getText().toString().replace(",", ".")));
            viewModel.getCurrentEvent().setCertificateHours(Integer.parseInt(binding.eventHours.getText().toString()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Timber.e("Value inserted is invalid");
        }
    }

    private void onNextEvent() {
       if (validFormData()) {
           if (VersionUtils.isLollipop()) {
               setExitTransition(new Slide(getGravityCompat(requireContext(), Gravity.START)));
               setAllowEnterTransitionOverlap(false);
               setAllowReturnTransitionOverlap(false);
           }
           controller.navigateToCreateEventFour(requireContext());
       }
    }

    private boolean validFormData() {
        boolean valid = true;

        boolean free = binding.cbEventFree.isChecked();
        boolean hCer = binding.cbProvidesHours.isChecked();

        String description = binding.eventLongDescription.getText().toString().trim();
        String organization = binding.eventOrganization.getText().toString().trim();

        double price = -1;
        int hours = -1;

        try {
            price = Double.parseDouble(binding.eventPrice.getText().toString().trim().replace(",", "."));
            hours = Integer.parseInt(binding.eventHours.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Timber.e("Value inserted is invalid");
        }

        if (!free && price < 0) {
            valid = false;
            binding.eventPrice.setError(getString(R.string.event_invalid_price));
            binding.eventPrice.requestFocus();
        }

        if (hCer && hours < 0) {
            valid = false;
            binding.eventHours.setError(getString(R.string.event_invalid_hours));
            binding.eventHours.requestFocus();
        }

        if (description.length() < 10) {
            valid = false;
            binding.eventLongDescription.setError(getString(R.string.validation_error_format, 10));
            binding.eventLongDescription.requestFocus();
        }

        if (organization.length() < 3) {
            valid = false;
            binding.eventOrganization.setError(getString(R.string.validation_error_format, 3));
            binding.eventOrganization.requestFocus();
        }

        return valid;
    }
}
