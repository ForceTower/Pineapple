package com.forcetower.uefs.view.universe.fragment;

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
import android.widget.Button;
import android.widget.ImageView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.universe.UniverseNavigationController;

import java.util.Collections;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by João Paulo on 11/05/2018.
 */
public class UniverseWelcomeStartFragment extends androidx.fragment.app.Fragment implements Injectable {
    @Inject
    UniverseNavigationController navigation;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_universe_start_welcome, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(value = R.id.btn_first_steps)
    public void onFirstStepsClicked() {
        if (VersionUtils.isLollipop()) {
            setExitTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, getResources().getConfiguration().getLayoutDirection())));
        }

        navigation.navigateToCreateAccount(Collections.singletonList(
                new Pair<>(getString(R.string.transition_logo), ivLogo)
        ));

    }

    @OnClick(value = R.id.btn_know_more)
    public void onKnowMoreClicked() {
        if (VersionUtils.isLollipop()) {
            setExitTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, getResources().getConfiguration().getLayoutDirection())));
        }

        navigation.navigateToKnowMore(requireContext());
    }
}
