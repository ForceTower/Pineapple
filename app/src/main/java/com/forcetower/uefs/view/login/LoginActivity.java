package com.forcetower.uefs.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.forcetower.uefs.R;
import com.forcetower.uefs.exception.LoginException;
import com.forcetower.uefs.helpers.PrefUtils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.view.UEFSBaseActivity;
import com.forcetower.uefs.view.connected.ConnectedActivity;
import com.forcetower.uefs.view.connected.NConnectedActivity;

import static com.forcetower.uefs.Constants.APP_TAG;

public class LoginActivity extends UEFSBaseActivity implements LoginViewCallback {
    private static final String LOGIN_FORM_TAG = "login form";
    private static final String CONNECTING_TAG = "connecting";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (!PrefUtils.getPrefs(this).contains("purge_profile_205")) {
            SagresPortalSDK.logout();
            PrefUtils.save(this, "purge_profile_205", true);
            Log.i(APP_TAG, "Profile purged for update!");
        }

        if (savedInstanceState != null)
            return;

        if (SagresAccess.getCurrentAccess() != null && SagresProfile.getCurrentProfile() == null) {
            replaceFragmentContainer(getSupportFragmentManager(), new ConnectingFragment(), R.id.fragment_container, CONNECTING_TAG);
            return;
        }

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LOGIN_FORM_TAG);
        if (fragment == null) fragment = new LoginFormFragment();

        replaceFragmentContainer(getSupportFragmentManager(), fragment, R.id.fragment_container, LOGIN_FORM_TAG);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onFinishInitializing() {
        super.onFinishInitializing();
        if (SagresAccess.getCurrentAccess() != null && SagresProfile.getCurrentProfile() != null) {
            onLoginSuccess();
        }
    }

    @Override
    public void onLoginClicked(String username, String password) {
        Fragment fragment = new ConnectingFragment();
        Bundle login = new Bundle();
        login.putString("username", username);
        login.putString("password", password);
        fragment.setArguments(login);
        replaceFragmentContainer(getSupportFragmentManager(), fragment, R.id.fragment_container, CONNECTING_TAG);
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(LOGIN_FORM_TAG);
        if (fragment == null) fragment = new LoginFormFragment();

        Bundle bundle = new Bundle();

        if (throwable != null) {
            if (throwable instanceof LoginException) {
                int idx = ((LoginException)throwable).getIdx();
                if (idx == LoginException.CONNECTION_ERROR)
                    bundle.putInt("error", 1);
                else if (idx == LoginException.INVALID_LOGIN)
                    bundle.putInt("error", 2);
            }
        }

        fragment.setArguments(bundle);
        replaceFragmentContainer(getSupportFragmentManager(), fragment, R.id.fragment_container, LOGIN_FORM_TAG);
    }

    @Override
    public void onLoginSuccess() {
        if (SagresAccess.getCurrentAccess() != null && SagresProfile.getCurrentProfile() != null) {
            Intent intent = new Intent(LoginActivity.this, ConnectedActivity.class);
            if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("lucas_layout", true))
                intent = new Intent(LoginActivity.this, NConnectedActivity.class);

            Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
            startActivity(intent, bundle);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        /*
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CONNECTING_TAG);
        if (fragment != null && fragment.isVisible() && fragment.isResumed()) {
            fragment = getSupportFragmentManager().findFragmentByTag(LOGIN_FORM_TAG);
            if (fragment == null) fragment = new LoginFormFragment();
            replaceFragmentContainer(getSupportFragmentManager(), fragment, R.id.fragment_container, LOGIN_FORM_TAG);
            return;
        }
        */
        super.onBackPressed();
    }
}
