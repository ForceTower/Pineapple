package com.forcetower.uefs.view.connected.fragments;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentEventCreationStartBinding;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.NavigationController;

import javax.inject.Inject;

import static com.forcetower.uefs.util.SupportUtils.getGravityCompat;

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
        if (VersionUtils.isLollipop()) {
            setExitTransition(new Slide(getGravityCompat(requireContext(), Gravity.START)));
            setAllowEnterTransitionOverlap(false);
            setAllowReturnTransitionOverlap(false);
        }
        controller.navigateToCreateEventOne(requireContext());
    }
}
