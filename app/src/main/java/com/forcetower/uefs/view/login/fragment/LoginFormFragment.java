package com.forcetower.uefs.view.login.fragment;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
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

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.forcetower.uefs.R;
import com.forcetower.uefs.anim.ChangeBoundsTransition;
import com.forcetower.uefs.databinding.FragmentLoginFormBinding;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.about.AboutActivity;
import com.forcetower.uefs.view.connected.LoggedActivity;
import com.forcetower.uefs.vm.base.LoginViewModel;
import com.forcetower.uefs.work.sync.SyncWorkerUtils;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by João Paulo on 06/03/2018.
 */

public class LoginFormFragment extends Fragment implements Injectable {
    public static final String TAG = "LoginFormFragment";

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    FirebaseJobDispatcher dispatcher;

    private LoginViewModel loginViewModel;
    private FragmentLoginFormBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_form, container, false);
        binding.btnLoginConnect.setOnClickListener(v -> onConnectButtonClicked());
        binding.tvClickToKnowAbout.setOnClickListener(v -> AboutActivity.startActivity(requireContext()));
        return binding.getRoot();
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
            AnimUtils.fadeOut(getContext(), binding.rlLoading);
            AnimUtils.fadeIn(getContext(), binding.cvLoginFormRoot);
            AnimUtils.fadeIn(getContext(), binding.tvClickToKnowAbout);
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
        String username = binding.etLoginUsername.getText().toString();
        String password = binding.etLoginPassword.getText().toString();

        if (username.trim().length() < 4) {
            binding.etLoginUsername.setError(getString(R.string.username_too_short));
            binding.etLoginUsername.requestFocus();
            return;
        } else if (password.length() < 4) {
            binding.etLoginPassword.setError(getString(R.string.password_too_short));
            binding.etLoginPassword.requestFocus();
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
                    .addSharedElement(binding.imageLoginLogo, "transition_logo")
                    .commit();
        } else {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment, ConnectingFragment.TAG)
                    .commit();
        }
    }
}
