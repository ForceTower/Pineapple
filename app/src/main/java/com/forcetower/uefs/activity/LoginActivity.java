package com.forcetower.uefs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.base.UEFSBaseActivity;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.managers.SagresLoginManager;

import okhttp3.Call;

public class LoginActivity extends UEFSBaseActivity {
    private Button btn_login;
    private EditText et_username;
    private EditText et_password;
    private ProgressBar progressBar;
    private TextView aboutApp;

    private Call currentCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        et_username = findViewById(R.id.username_form);
        et_password = findViewById(R.id.password_form);
        progressBar = findViewById(R.id.login_progress);
        aboutApp = findViewById(R.id.about_app);

        if (Utils.isLollipop()) btn_login.setElevation(3);

        if (SagresAccess.getCurrentAccess() != null && SagresProfile.getCurrentProfile() == null) {
            Toast.makeText(this, R.string.connecting_you_dont_need_to_relog, Toast.LENGTH_SHORT).show();
            ParsingActivity.startActivity(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onFinishInitializing() {
        super.onFinishInitializing();
        if (SagresAccess.getCurrentAccess() != null && SagresProfile.getCurrentProfile() != null) {
            //ConnectedActivity.startActivity(this);
            Intent intent = new Intent(LoginActivity.this, NConnectedActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void login(View view) {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        if (username.length() < 4)
            et_username.setError(getString(R.string.invalid_username));
        else if (password.length() < 4)
            et_password.setError(getString(R.string.invalid_password));
        else {
            btn_login.setClickable(false);
            btn_login.setAlpha(0.4f);
            btn_login.setText(R.string.connecting);
            progressBar.setVisibility(View.VISIBLE);
            et_username.setEnabled(false);
            et_password.setEnabled(false);
            Utils.fadeOut(aboutApp, this);
            connectToPortal(username, password);
        }
    }

    private void connectToPortal(final String username, final String password) {
        SagresLoginManager.getInstance().login(username, password, new SagresLoginManager.SagresLoginCallback() {
            @Override
            public void onSuccess() {
                Log.i(Constants.APP_TAG, "LoginActivity::ParseSuccess()");
                Intent intent = new Intent(LoginActivity.this, NConnectedActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(boolean failedConnection) {
                if (failedConnection) {
                    Log.i(Constants.APP_TAG, "LoginActivity::Network Error");
                    returnUiToDefault(false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, R.string.connection_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.i(Constants.APP_TAG, "LoginActivity::Invalid Login");
                    returnUiToDefault(true);
                }
            }

            @Override
            public void onLoginSuccess() {
                ParsingActivity.startActivity(LoginActivity.this);
            }
        });
    }

    private void returnUiToDefault(final boolean error) {
        currentCall = null;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_login.setClickable(true);
                btn_login.setAlpha(1f);
                btn_login.setText(R.string.login_btn);
                progressBar.setVisibility(View.INVISIBLE);
                et_username.setEnabled(true);
                et_password.setEnabled(true);
                Utils.fadeIn(aboutApp, LoginActivity.this);

                if (error)
                    et_password.setError(getString(R.string.incorrect_credentials));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (currentCall != null && !currentCall.isExecuted()) {
            currentCall.cancel();

            returnUiToDefault(false);
        } else {
            super.onBackPressed();
        }
    }

    public void about(View view) {
        AboutActivity.startActivity(this);
    }
}
