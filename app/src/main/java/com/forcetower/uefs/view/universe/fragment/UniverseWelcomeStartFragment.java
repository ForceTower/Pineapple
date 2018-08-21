package com.forcetower.uefs.view.universe.fragment;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;
import androidx.core.view.GravityCompat;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentUniverseStartWelcomeBinding;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.universe.UniverseNavigationController;

import java.util.Collections;

import javax.inject.Inject;

/**
 * Created by João Paulo on 11/05/2018.
 */
public class UniverseWelcomeStartFragment extends Fragment implements Injectable {
    @Inject
    UniverseNavigationController navigation;

    private FragmentUniverseStartWelcomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_universe_start_welcome, container, false);
        binding.btnFirstSteps.setOnClickListener(v -> onFirstStepsClicked());
        binding.btnKnowMore.setOnClickListener(v -> onKnowMoreClicked());
        return binding.getRoot();
    }

    public void onFirstStepsClicked() {
        if (VersionUtils.isLollipop()) {
            setExitTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, getResources().getConfiguration().getLayoutDirection())));
        }

        navigation.navigateToCreateAccount(Collections.singletonList(
                new Pair<>(getString(R.string.transition_logo), binding.ivLogo)
        ));

    }

    public void onKnowMoreClicked() {
        if (VersionUtils.isLollipop()) {
            setExitTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, getResources().getConfiguration().getLayoutDirection())));
        }

        navigation.navigateToKnowMore(requireContext());
    }
}
