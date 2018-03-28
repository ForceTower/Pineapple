package com.forcetower.uefs.view.settings;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.forcetower.uefs.R;
import com.forcetower.uefs.alm.RefreshAlarmTrigger;
import com.forcetower.uefs.rep.LoginRepository;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.login.MainActivity;

import javax.inject.Inject;

import butterknife.BindView;
import dagger.android.AndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class SettingsActivity extends UBaseActivity implements SettingsController, HasSupportFragmentInjector {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    LoginRepository repository;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_settings, savedInstanceState);
        setupToolbar();

        getFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();
    }

    public void logout() {
        RefreshAlarmTrigger.disableBootComponent(this);
        RefreshAlarmTrigger.removeAlarm(this);
        repository.logout().observe(this, resource -> {
            //noinspection ConstantConditions
            if (resource.status == Status.SUCCESS) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ActivityCompat.finishAffinity(this);
            }
        });
    }

    public Context getContext() {
        return getApplicationContext();
    }

    private void setupToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (VersionUtils.isLollipop()) toolbar.setElevation(10);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.settings);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return null;
    }
}
