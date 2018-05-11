package com.forcetower.uefs.view.universe.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.VersionUtils;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

/**
 * Created by João Paulo on 11/05/2018.
 */
public class UniverseCreateAccountFragment extends Fragment implements Injectable {
    @BindView(R.id.view_root)
    ViewGroup rootContainer;
    @BindViews({R.id.dev_iv_anim_1, R.id.dev_iv_anim_2, R.id.dev_iv_anim_3, R.id.dev_iv_anim_4})
    View[] views;
    @BindView(R.id.ll_animations)
    ViewGroup llAnimation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_universe_create_account, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        beginAnimations();
    }

    private void beginAnimations() {
        if (!VersionUtils.isLollipop()) return;

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            TransitionManager.beginDelayedTransition(rootContainer, new TransitionSet()
                    .addTransition(new ChangeBounds())
                    .addTransition(new ChangeImageTransform()));

            llAnimation.setVisibility(View.VISIBLE);
            fadeInViews(0);
        }, 1500);
    }

    private void fadeInViews(int pos) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (views != null && pos < views.length) {
                if (isAdded() && isVisible() && isResumed() && !isHidden()) {
                    AnimUtils.slideIn(getContext(), views[pos]);
                    fadeInViews(pos + 1);
                }
            }
        }, 750);
    }
}
