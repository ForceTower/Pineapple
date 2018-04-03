package com.forcetower.uefs.view.control_room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.forcetower.uefs.BuildConfig;
import com.forcetower.uefs.R;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.control_room.fragments.MasterSyncControlFragment;

import javax.inject.Inject;

import butterknife.BindView;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class ControlRoomActivity extends UBaseActivity implements HasSupportFragmentInjector {
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ControlRoomActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_control_room, savedInstanceState);

        setupToolbar();

        if (savedInstanceState == null) {
            navigateToSyncControl();
        }
    }

    private void setupToolbar() {
        if (VersionUtils.isLollipop()) {
            appBarLayout.setElevation(10);
        }

        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_update_control);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void navigateToSyncControl() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new MasterSyncControlFragment())
                .commitAllowingStateLoss();
    }

    private void navigateToNoPermissions() {

    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
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
}
