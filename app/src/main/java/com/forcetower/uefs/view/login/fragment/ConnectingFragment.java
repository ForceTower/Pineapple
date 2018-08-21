package com.forcetower.uefs.view.login.fragment;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.anim.ChangeBoundsTransition;
import com.forcetower.uefs.databinding.FragmentLoginConnectingBinding;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.LoggedActivity;
import com.forcetower.uefs.vm.base.LoginViewModel;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by João Paulo on 06/03/2018.
 */

public class ConnectingFragment extends Fragment implements Injectable {
    public static final String TAG = "ConnectingFragment";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    AppExecutors executors;

    LoginViewModel loginViewModel;

    private boolean shouldCall = true;
    private FragmentLoginConnectingBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_connecting, container, false);
        animateLogo();
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
        loginViewModel.getLogin().observe(this, this::onLoginProgress);

        if (savedInstanceState != null)
            shouldCall = savedInstanceState.getBoolean("should_call", false);

        login();
    }

    private void login() {
        Bundle login = getArguments();
        if (login == null) {
            Timber.d("Login bundle is null...");
            Crashlytics.log("Login bundle is null");
            return;
        }
        String user = login.getString("username");
        String pass = login.getString("password");

        if (shouldCall) {
            executors.diskIO().execute(() -> {
                loginViewModel.deleteDatabase();
                loginViewModel.getLogin(user, pass);
                shouldCall = false;
            });
            loginViewModel.setActivityStarted(false);
        } else {
            Timber.d("Should not call");
        }
    }

    private void onLoginProgress(Resource<Integer> resource) {
        if (resource.status == Status.SUCCESS) {
            if (!loginViewModel.isActivityStarted()) {
                LoggedActivity.startActivity(getContext(), true);
                loginViewModel.setActivityStarted(true);
            } else {
                Timber.d("Activity already running!");
            }
            requireActivity().finish();
        } else if (resource.status == Status.LOADING) {
            Timber.d("Loading...");
            if (resource.data != null) {
                Timber.d("Message: %s", getString(resource.data));
                binding.tvLoginMessage.setText(resource.data);
            }
            else
                Timber.d("Resource data is null... Thinking...");
        } else {
            Timber.d("Failed :(");
            if (resource.data != null) {
                binding.tvLoginMessage.setText(resource.data);
            }

            if (resource.code == 401) {
                Toast.makeText(getContext(), R.string.invalid_login, Toast.LENGTH_SHORT).show();
            }

            new Handler().postDelayed(this::goToLoginPage, 1000);
        }
    }

    private void goToLoginPage() {
        if (!isAdded() || isDetached()) return;
        if (!getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
            if (getArguments() != null) {
                String user = getArguments().getString("username");
                Crashlytics.log("Can we get some haha's for this guy? " + user);
            }
            return;
        }

        Fragment fragment = requireActivity().getSupportFragmentManager().findFragmentByTag(LoginFormFragment.TAG);
        if (fragment == null) {
            fragment = new LoginFormFragment();
        }

        if (VersionUtils.isLollipop()) {
            fragment.setSharedElementEnterTransition(new ChangeBoundsTransition());
            fragment.setSharedElementReturnTransition(new ChangeBoundsTransition());
            setExitTransition(new Fade());

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .addSharedElement(binding.imageLoginLogo, "transition_logo")
                    .replace(R.id.container, fragment, LoginFormFragment.TAG)
                    .commit();
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, LoginFormFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean("should_call", shouldCall);
        super.onSaveInstanceState(outState);
    }

    private void animateLogo() {
        AlphaAnimation fade = new AlphaAnimation(0.65f, 1f);
        fade.setRepeatMode(Animation.REVERSE);
        fade.setDuration(750);
        fade.setRepeatCount(Animation.INFINITE);
        binding.imageLoginLogo.startAnimation(fade);
        if (VersionUtils.isLollipop()) binding.imageLoginLogo.setElevation(5);
    }
}
