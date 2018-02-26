package com.forcetower.uefs.view.connected;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.database.AppDatabase;
import com.forcetower.uefs.helpers.SyncUtils;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.view.UEFSBaseActivity;
import com.forcetower.uefs.view.settings.SettingsActivity;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectedActivity extends UEFSBaseActivity {
    private static final String TAG = "ConnectedActivity";
    private boolean doubleBack = false;

    @Inject
    AppDatabase database;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

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
        ButterKnife.bind(this);
        ((UEFSApplication) getApplication()).getApplicationComponent().inject(this);

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
            changeToolbarText(R.string.title_schedule);
        }
        Log.d(TAG, "onCreate: ");
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
        Log.d(TAG, "onCreateOptionsMenu: ");
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        new Handler(getMainLooper()).postDelayed(this::featureDiscovery, 500);
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

    public void featureDiscovery() {
        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forToolbarMenuItem(toolbar, R.id.menu_class_review, getString(R.string.class_review_discover_title), getString(R.string.menu_class_review_discover_text))
                        .outerCircleColor(R.color.colorPrimary)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.white)   // Specify a color for the target circle
                        .titleTextSize(20)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.white)      // Specify the color of the title text
                        .descriptionTextSize(10)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.white)  // Specify the color of the description text
                        .textColor(R.color.white)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                        //.icon(Drawable)                     // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional
                    }
                });
    }
}
