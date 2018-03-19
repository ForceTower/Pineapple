package com.forcetower.uefs.view.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import com.forcetower.uefs.R;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.login.fragment.LoginFormFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

public class MainActivity extends UBaseActivity implements HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_main, savedInstanceState);

        if (savedInstanceState == null) {
            updateReset();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new LoginFormFragment())
                    .commit();
        }
    }

    private void updateReset() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (!preferences.contains("update_v_3.0.0.rc1")) {
            Timber.d("Performing full clear");
            preferences.edit().clear().apply();
            ((UEFSApplication)getApplication()).clearApplicationData();
            preferences.edit().putInt("update_v_3.0.0.rc1", 1).apply();
        }

    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
