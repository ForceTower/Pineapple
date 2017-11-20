package com.forcetower.uefs.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.base.UEFSBaseActivity;
import com.forcetower.uefs.fragments.MessageBoardFragment;
import com.forcetower.uefs.fragments.ScheduleFragment;
import com.forcetower.uefs.helpers.SyncUtils;

public class ConnectedActivity extends UEFSBaseActivity {
    private static final String AUTHORITY = "com.forcetower.uefs.providers";

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ConnectedActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

        context.startActivity(intent, bundle);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                changeToolbarText(R.string.title_schedule);
                switchToFragment(ScheduleFragment.class);
                return true;
            } else if (id == R.id.navigation_dashboard) {
                changeToolbarText(R.string.title_messages);
                switchToFragment(MessageBoardFragment.class);
                return true;
            } else if (id == R.id.navigation_notifications) {
                changeToolbarText(R.string.title_notifications);
                return true;
            }
            return false;
        }
    };

    private void changeToolbarText(@StringRes int resId) {
        /*if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(resId);
        }*/
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        BottomNavigationView navigation = findViewById(R.id.navigation);

        SyncUtils.createSyncAccount(this);
/*
        if (Utils.supportsMaterialDesign()) {
            getWindow().setEnterTransition(new Fade());
            getWindow().setExitTransition(new Explode());
        }
*/

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            ScheduleFragment fragment = ScheduleFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
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
}
