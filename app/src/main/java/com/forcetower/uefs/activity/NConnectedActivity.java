package com.forcetower.uefs.activity;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.internal.ScrimInsetsFrameLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.base.UEFSBaseActivity;
import com.forcetower.uefs.adapters.ui.NavDrawerItem;
import com.forcetower.uefs.fragments.CalendarFragment;
import com.forcetower.uefs.fragments.DisciplinesFragment;
import com.forcetower.uefs.fragments.GradesFragment;
import com.forcetower.uefs.fragments.MessageBoardFragment;
import com.forcetower.uefs.fragments.NavigationDrawerFragment;
import com.forcetower.uefs.fragments.ScheduleFragment;
import com.forcetower.uefs.helpers.SyncUtils;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.managers.SagresProfileManager;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 29/11/2017.
 */

public class NConnectedActivity extends UEFSBaseActivity implements NavigationDrawerFragment.Callbacks{
    private Toolbar toolbar;
    private ScrimInsetsFrameLayout navigationDrawerContainer;
    private NavigationDrawerFragment navigationDrawerFragment;

    private boolean doubleBack;

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

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof ScheduleFragment)) {
            if (Utils.isLollipop()) fragment.setExitTransition(new Fade(Fade.OUT));
            switchToFragment(ScheduleFragment.class, true);
            navigationDrawerFragment.selectItem(0);
            return;
        }

        if (doubleBack || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("double_back", false)) {
            super.onBackPressed();
            return;
        }

        this.doubleBack = true;
        Toast.makeText(this, R.string.press_back_twice, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBack = false, 2000);
    }

    @Override
    public void onNavigationDrawerItemSelected(NavDrawerItem item) {
        if (item == null)
            return;

        int tag = item.getTag();
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(item.getTitle());
        if (tag == 1) {
            switchToFragment(ScheduleFragment.class, false);
        } else if (tag == 2) {
            switchToFragment(MessageBoardFragment.class, false);
        } else if (tag == 3) {
            switchToFragment(GradesFragment.class, false);
        } else if (tag == 4) {
            switchToFragment(CalendarFragment.class, false);
        } else if (tag == 5) {
            switchToFragment(DisciplinesFragment.class, false);
        }
    }

    private void switchToFragment(Class clazz, boolean animate) {
        String tag = clazz.getName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null || tag.equals("GradesFragment")) {
            try {
                fragment = (Fragment) clazz.newInstance();
                if (animate && Utils.isLollipop()) fragment.setEnterTransition(new Fade(Fade.IN));
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (animate && Utils.isLollipop()) fragment.setEnterTransition(new Fade(Fade.IN));
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

    @Override
    protected void onResume() {
        super.onResume();
    }
}
