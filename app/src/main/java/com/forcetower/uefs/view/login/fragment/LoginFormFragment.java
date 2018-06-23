package com.forcetower.uefs.view.login.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.anim.ChangeBoundsTransition;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.about.AboutActivity;
import com.forcetower.uefs.view.connected.LoggedActivity;
import com.forcetower.uefs.vm.base.LoginViewModel;
import com.forcetower.uefs.work.sync.SyncWorkerUtils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by João Paulo on 06/03/2018.
 */

public class LoginFormFragment extends Fragment implements Injectable {
    public static final String TAG = "LoginFormFragment";

    @BindView(R.id.et_login_username)
    EditText etUsername;
    @BindView(R.id.et_login_password)
    EditText etPassword;
    @BindView(R.id.btn_login_connect)
    Button btnConnect;
    @BindView(R.id.tv_click_to_know_about)
    TextView tvClickToKnowAbout;

    @BindView(R.id.image_login_logo)
    ImageView ivLogo;
    @BindView(R.id.rl_loading)
    ViewGroup vgLoading;
    @BindView(R.id.cv_login_form_root)
    ViewGroup vgForm;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private LoginViewModel loginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_form, container, false);
        ButterKnife.bind(this, view);
        btnConnect.setOnClickListener(v -> onConnectButtonClicked());
        tvClickToKnowAbout.setOnClickListener(v -> AboutActivity.startActivity(requireContext()));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
        loginViewModel.getAccess().observe(this, this::onReceiveAccess);
    }

    private void onReceiveAccess(Access access) {
        if (access == null) {
            Timber.d("Access is null. Enabling Login form");
            AnimUtils.fadeOut(getContext(), vgLoading);
            AnimUtils.fadeIn(getContext(), vgForm);
            AnimUtils.fadeIn(getContext(), tvClickToKnowAbout);
            SyncWorkerUtils.disableWorker(requireContext());
        } else {
            Timber.d("Access is not null. Moving to connected!");
            if (!loginViewModel.isActivityStarted()) {
                LoggedActivity.startActivity(getContext(), false);
                loginViewModel.setActivityStarted(true);
            }
            requireActivity().finish();
        }
    }

    private void onConnectButtonClicked() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        if (username.trim().length() < 4) {
            etUsername.setError(getString(R.string.username_too_short));
            etUsername.requestFocus();
            return;
        } else if (password.length() < 4) {
            etPassword.setError(getString(R.string.password_too_short));
            etPassword.requestFocus();
            return;
        }

        login(username, password);
    }

    private void login(String username, String password) {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);

        Fragment fragment = new ConnectingFragment();
        fragment.setArguments(bundle);

        if (VersionUtils.isLollipop()) {
            fragment.setSharedElementEnterTransition(new ChangeBoundsTransition());
            fragment.setSharedElementReturnTransition(new ChangeBoundsTransition());
            setExitTransition(new Fade());

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, ConnectingFragment.TAG)
                    .addSharedElement(ivLogo, "transition_logo")
                    .commit();
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, ConnectingFragment.TAG)
                    .commit();
        }
    }
}
