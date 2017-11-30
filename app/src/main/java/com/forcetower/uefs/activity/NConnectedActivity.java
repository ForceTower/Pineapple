package com.forcetower.uefs.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.base.UEFSBaseActivity;
import com.forcetower.uefs.adapters.ui.NavDrawerItem;
import com.forcetower.uefs.fragments.MessageBoardFragment;
import com.forcetower.uefs.fragments.NavigationDrawerFragment;
import com.forcetower.uefs.fragments.ScheduleFragment;
import com.forcetower.uefs.fragments.SettingsFragment;
import com.forcetower.uefs.helpers.SyncUtils;
import com.forcetower.uefs.helpers.Utils;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 29/11/2017.
 */

public class NConnectedActivity extends UEFSBaseActivity implements NavigationDrawerFragment.Callbacks{
    private Toolbar toolbar;
    private ScrimInsetsFrameLayout navigationDrawerContainer;
    private NavigationDrawerFragment navigationDrawerFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected_new);

        toolbar = findViewById(R.id.toolbar);
        navigationDrawerContainer = findViewById(R.id.navigation_drawer_container);
        navigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer_fragment);

        if (Utils.isLollipop()) {
            toolbar.setElevation(10);
        }
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        SyncUtils.createSyncAccount(this);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        navigationDrawerFragment.init(navigationDrawerContainer, drawerLayout);

        if (savedInstanceState == null)
            navigationDrawerFragment.selectItem(0);
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawerFragment != null && !navigationDrawerFragment.isDetached() && navigationDrawerFragment.isVisible()) {
            if (navigationDrawerFragment.onBackPressed())
                return;
        }
        super.onBackPressed();
    }

    @Override
    public void onNavigationDrawerItemSelected(NavDrawerItem item) {
        if (item == null)
            return;

        int tag = item.getTag();
        if (tag == 1) {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(item.getTitle());
            switchToFragment(ScheduleFragment.class);
        } else if (tag == 2) {
            if (getSupportActionBar() != null) getSupportActionBar().setTitle(item.getTitle());
            switchToFragment(MessageBoardFragment.class);
        }
    }

    private void switchToFragment(Class clazz) {
        String tag = clazz.getName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            try {
                fragment = (Fragment) clazz.newInstance();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, tag).commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                return false;
        }
        return super.onOptionsItemSelected(item);
    }
}
