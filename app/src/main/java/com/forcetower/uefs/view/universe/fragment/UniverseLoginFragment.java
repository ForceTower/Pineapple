package com.forcetower.uefs.view.universe.fragment;

import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputEditText;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.FragmentUniverseLoginBinding;
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
import timber.log.Timber;

/**
 * Created by João Paulo on 12/05/2018.
 */
public class UniverseLoginFragment extends Fragment implements Injectable {
    @Inject
    UniverseNavigationController navigation;
    @Inject
    UEFSViewModelFactory viewModelFactory;

    private UAccountViewModel accountViewModel;
    private FragmentUniverseLoginBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_universe_login, container, false);
        binding.btnCancel.setOnClickListener(v -> onCancelClick());
        binding.btnUniverseLogin.setOnClickListener(v -> onLoginClicked());
        return binding.getRoot();
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
                    new Pair<>(getString(R.string.transition_logo), binding.ivLogo)
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

    public void onLoginClicked() {
        String username = binding.etUniverseUsername.getText().toString();
        String password = binding.etUniversePassword.getText().toString();

        if (username.trim().length() < 4) {
            binding.etUniverseUsername.setError(getString(R.string.username_too_short));
            binding.etUniverseUsername.requestFocus();
            return;
        }

        if (password.trim().length() < 4) {
            binding.etUniversePassword.setError(getString(R.string.password_too_short));
            binding.etUniversePassword.requestFocus();
            return;
        }

        accountViewModel.loginWith(username, password);
    }

    public void onCancelClick() {
        requireActivity().onBackPressed();
    }

    private void beginAnimations() {
        if (!VersionUtils.isLollipop()) {
            binding.llAnimations.setVisibility(View.VISIBLE);
            for (int i = 0; i < binding.llAnimations.getChildCount(); i++) {
                View child = binding.llAnimations.getChildAt(i);
                child.setVisibility(View.VISIBLE);
            }
        } else {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                TransitionManager.beginDelayedTransition(binding.viewRoot, new TransitionSet()
                        .addTransition(new ChangeBounds())
                        .addTransition(new ChangeImageTransform()));

                binding.llAnimations.setVisibility(View.VISIBLE);
                fadeInViews(0);
            }, 750);
        }
    }

    private void fadeInViews(int pos) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (pos < binding.llAnimations.getChildCount()) {
                if (isAdded() && isVisible() && isResumed() && !isHidden()) {
                    AnimUtils.slideIn(getContext(), binding.llAnimations.getChildAt(pos));
                    fadeInViews(pos + 1);
                }
            }
        }, 400);
    }
}
