package com.forcetower.uefs.view.universe.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db_service.entity.AccessToken;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.universe.UniverseNavigationController;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.universe.UAccountViewModel;

import java.util.Collections;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by João Paulo on 12/05/2018.
 */
public class UniverseLoginFragment extends Fragment implements Injectable {
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.view_root)
    ViewGroup rootContainer;
    @BindView(R.id.ll_animations)
    ViewGroup llAnimation;
    @BindViews({R.id.dev_iv_anim_1, R.id.dev_iv_anim_2, R.id.dev_iv_anim_3, R.id.dev_iv_anim_4})
    View[] views;
    @BindView(R.id.et_universe_username)
    TextInputEditText etUsername;
    @BindView(R.id.et_universe_password)
    TextInputEditText etPassword;

    @Inject
    UniverseNavigationController navigation;
    @Inject
    UEFSViewModelFactory viewModelFactory;

    private UAccountViewModel accountViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_universe_login, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        beginAnimations();
        accountViewModel = ViewModelProviders.of(this, viewModelFactory).get(UAccountViewModel.class);
        accountViewModel.getAccessToken().observe(this, this::onAccessTokenReceived);
        accountViewModel.getLoginWith().observe(this, this::onLoginProgress);
    }

    private void onLoginProgress(Resource<AccessToken> tokenResource) {
        if (tokenResource == null) return;

        if (tokenResource.status == Status.SUCCESS) {
            Timber.d("Resource Success: Token received!");
            navigation.navigateToCompleted(Collections.singletonList(
                    new Pair<>(getString(R.string.transition_logo), ivLogo)
            ), requireContext());
        } else if (tokenResource.status == Status.ERROR) {
            Timber.d("Resource error code: %d", tokenResource.code);
            Timber.d("Resource error message: %s", tokenResource.message);
        } else {
            Timber.d("Loading resource...");
        }
    }

    private void onAccessTokenReceived(AccessToken token) {
        if (token != null && token.isValid() && !token.isExpired()) {
            Timber.d("Received token... App can proceed");
        }
    }

    @OnClick(value = R.id.btn_universe_login)
    public void onLoginClicked() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (username.trim().length() < 4) {
            etUsername.setError(getString(R.string.username_too_short));
            etUsername.requestFocus();
            return;
        }

        if (password.trim().length() < 4) {
            etPassword.setError(getString(R.string.password_too_short));
            etPassword.requestFocus();
            return;
        }

        accountViewModel.loginWith(username, password);
    }

    @OnClick(value = R.id.btn_cancel)
    public void onCancelClick() {
        requireActivity().onBackPressed();
    }

    private void beginAnimations() {
        if (!VersionUtils.isLollipop()) {
            llAnimation.setVisibility(View.VISIBLE);
            for (View view : views) view.setVisibility(View.VISIBLE);
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                TransitionManager.beginDelayedTransition(rootContainer, new TransitionSet()
                        .addTransition(new ChangeBounds())
                        .addTransition(new ChangeImageTransform()));

                llAnimation.setVisibility(View.VISIBLE);
                fadeInViews(0);
            }, 750);
        }
    }

    private void fadeInViews(int pos) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (views != null && pos < views.length) {
                if (isAdded() && isVisible() && isResumed() && !isHidden()) {
                    AnimUtils.slideIn(getContext(), views[pos]);
                    fadeInViews(pos + 1);
                }
            }
        }, 400);
    }
}
