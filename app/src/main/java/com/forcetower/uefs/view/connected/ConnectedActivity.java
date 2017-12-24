package com.forcetower.uefs.view.connected;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.SettingsActivity;
import com.forcetower.uefs.activity.base.UEFSBaseActivity;
import com.forcetower.uefs.fragments.CalendarFragment;
import com.forcetower.uefs.fragments.DisciplinesFragment;
import com.forcetower.uefs.fragments.GradesFragment;
import com.forcetower.uefs.fragments.MessageBoardFragment;
import com.forcetower.uefs.fragments.ScheduleFragment;
import com.forcetower.uefs.helpers.SyncUtils;
import com.forcetower.uefs.helpers.Utils;

public class ConnectedActivity extends UEFSBaseActivity {
    private static final String SCHEDULE_TAG = "schedule";
    private boolean doubleBack = false;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ConnectedActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

        context.startActivity(intent, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));

        BottomNavigationView navigation = findViewById(R.id.navigation);

        if (Utils.isLollipop()) {
            toolbar.setElevation(10);
            navigation.setElevation(10);
        }
        
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        setSupportActionBar(toolbar);
        SyncUtils.createSyncAccount(this);

        navigation.setOnNavigationItemSelectedListener(this::onItemSelected);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            switchToFragment(ScheduleFragment.class);
        }
    }

    private boolean onItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navigation_home) {
            changeToolbarText(R.string.title_schedule);
            switchToFragment(ScheduleFragment.class);
            return true;
        } else if (id == R.id.navigation_dashboard) {
            changeToolbarText(R.string.title_messages);
            switchToFragment(MessageBoardFragment.class);
            return true;
        } else if (id == R.id.navigation_grades) {
            changeToolbarText(R.string.title_grades);
            switchToFragment(GradesFragment.class);
            return true;
        } else if (id == R.id.navigation_disciplines) {
            changeToolbarText(R.string.title_disciplines);
            switchToFragment(DisciplinesFragment.class);
            return true;
        } else if (id == R.id.navigation_calendar) {
            changeToolbarText(R.string.title_calendar);
            switchToFragment(CalendarFragment.class);
            return true;
        }
        return false;
    }

    private void changeToolbarText(@StringRes int resId) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(resId);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connected, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            SettingsActivity.startActivity(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        /*Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (!(fragment instanceof ScheduleFragment)) {
            if (Utils.isLollipop()) fragment.setExitTransition(new Fade(Fade.OUT));
            switchToFragment(ScheduleFragment.class);
            return;
        }*/

        if (doubleBack || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("double_back", false)) {
            super.onBackPressed();
            return;
        }

        this.doubleBack = true;
        Toast.makeText(this, R.string.press_back_twice, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBack = false, 2000);
    }
}
