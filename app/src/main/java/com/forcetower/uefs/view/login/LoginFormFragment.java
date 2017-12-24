package com.forcetower.uefs.view.login;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 24/12/2017.
 */
public class LoginFormFragment extends Fragment {
    @BindView(R.id.username_form)
    TextView textUsername;
    @BindView(R.id.password_form)
    TextView textPassword;

    private LoginViewCallback callback;

    public LoginFormFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_form, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_login)
    public void login() {
        String username = textUsername.getText().toString();
        String password = textPassword.getText().toString();

        if (username.trim().length() < 4) {
            textUsername.setError(getString(R.string.invalid_username));
            return;
        } else if (password.trim().length() < 4) {
            textPassword.setError(getString(R.string.invalid_password));
            return;
        }

        callback.onLoginClicked(username, password);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int error = bundle.getInt("error", -1);
            if (error == 1) textPassword.setError(getString(R.string.incorrect_credentials));
            else if (error == 2) Toast.makeText(getContext(), R.string.connection_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (LoginViewCallback) context;
        } catch (ClassCastException e) {
            Log.e(APP_TAG, "onAttach: must implement LoginViewCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
