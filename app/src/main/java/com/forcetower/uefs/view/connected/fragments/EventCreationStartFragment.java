package com.forcetower.uefs.view.connected.fragments;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentEventCreationStartBinding;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.view.connected.NavigationController;

import javax.inject.Inject;

/**
 * Created by João Paulo on 16/06/2018.
 */
public class EventCreationStartFragment extends Fragment implements Injectable {
    @Inject
    NavigationController controller;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentEventCreationStartBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_event_creation_start, container, false);
        binding.btnContinue.setOnClickListener(v -> onContinueClick());
        return binding.getRoot();
    }

    private void onContinueClick() {
        controller.navigateToCreateEventOne();
    }
}
