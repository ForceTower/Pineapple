package com.forcetower.uefs.view.login.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.anim.ChangeBoundsTransition;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.connected.ConnectedActivity;
import com.forcetower.uefs.vm.LoginViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by João Paulo on 06/03/2018.
 */

public class ConnectingFragment extends Fragment implements Injectable {
    public static final String TAG = "ConnectingFragment";

    @BindView(R.id.image_login_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_login_message)
    TextView tvLoginMessage;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    AppExecutors executors;

    LoginViewModel loginViewModel;

    private boolean shouldCall = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_connecting, container, false);
        ButterKnife.bind(this, view);
        animateLogo();
        return view;
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
                ConnectedActivity.startActivity(getContext(), true);
                loginViewModel.setActivityStarted(true);
            } else {
                Timber.d("Activity already running!");
            }
            getActivity().finish();
        } else if (resource.status == Status.LOADING) {
            Timber.d("Loading...");
            if (resource.data != null) {
                Timber.d("Message: %s", getString(resource.data));
                tvLoginMessage.setText(resource.data);
            }
            else
                Timber.d("Resource data is null... Thinking...");
        } else {
            Timber.d("Failed :(");
            if (resource.data != null) {
                tvLoginMessage.setText(resource.data);
            }

            if (resource.code == 401) {
                Toast.makeText(getContext(), R.string.invalid_login, Toast.LENGTH_SHORT).show();
            }

            new Handler().postDelayed(this::goToLoginPage, 1000);
        }
    }

    private void goToLoginPage() {
        if (!isAdded() || isDetached()) return;

        Fragment fragment = getActivity().getSupportFragmentManager().findFragmentByTag(LoginFormFragment.TAG);
        if (fragment == null) {
            fragment = new LoginFormFragment();
        }

        if (VersionUtils.isLollipop()) {
            fragment.setSharedElementEnterTransition(new ChangeBoundsTransition());
            fragment.setSharedElementReturnTransition(new ChangeBoundsTransition());
            setExitTransition(new Fade());

            getActivity().getSupportFragmentManager().beginTransaction()
                    .addSharedElement(ivLogo, "transition_logo")
                    .replace(R.id.container, fragment, LoginFormFragment.TAG)
                    .commit();
        } else {
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, LoginFormFragment.TAG)
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("should_call", shouldCall);
        super.onSaveInstanceState(outState);
    }

    private void animateLogo() {
        AlphaAnimation fade = new AlphaAnimation(0.65f, 1f);
        fade.setRepeatMode(Animation.REVERSE);
        fade.setDuration(750);
        fade.setRepeatCount(Animation.INFINITE);
        ivLogo.startAnimation(fade);
        if (VersionUtils.isLollipop()) ivLogo.setElevation(5);
    }
}
