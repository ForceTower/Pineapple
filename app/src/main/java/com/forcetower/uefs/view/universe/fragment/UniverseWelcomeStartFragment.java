package com.forcetower.uefs.view.universe.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
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
public class UniverseWelcomeStartFragment extends Fragment implements Injectable {
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
            setAllowEnterTransitionOverlap(false);
            setAllowReturnTransitionOverlap(true);
        }

        navigation.navigateToCreateAccount(Collections.singletonList(
                new Pair<>(getString(R.string.transition_logo), ivLogo)
        ));

    }
}
