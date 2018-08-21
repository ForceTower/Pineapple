package com.forcetower.uefs.view.control_room;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ActivityControlRoomBinding;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.control_room.fragments.ControlRoomSelectionFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class ControlRoomActivity extends UBaseActivity implements HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    private ActivityControlRoomBinding binding;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ControlRoomActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_control_room);

        setupToolbar();

        if (savedInstanceState == null) {
            navigateToSyncControl();
        }
    }

    private void setupToolbar() {
        if (VersionUtils.isLollipop()) {
            binding.incToolbar.appBarLayout.setElevation(10);
        }

        setSupportActionBar(binding.incToolbar.toolbar);
        binding.incToolbar.toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_master_configuration);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void navigateToSyncControl() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new ControlRoomSelectionFragment())
                .commitAllowingStateLoss();
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
