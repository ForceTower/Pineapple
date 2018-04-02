package com.forcetower.uefs.view.connected;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.alm.RefreshAlarmTrigger;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.Version;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.connected.fragments.AllSemestersGradeFragment;
import com.forcetower.uefs.view.connected.fragments.AutoSyncFragment;
import com.forcetower.uefs.view.connected.fragments.BigTrayFragment;
import com.forcetower.uefs.view.connected.fragments.CalendarFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplinesFragment;
import com.forcetower.uefs.view.connected.fragments.MessagesFragment;
import com.forcetower.uefs.view.connected.fragments.NewScheduleFragment;
import com.forcetower.uefs.view.connected.fragments.ProfileFragment;
import com.forcetower.uefs.view.connected.fragments.ScheduleFragment;
import com.forcetower.uefs.view.login.MainActivity;
import com.forcetower.uefs.view.settings.SettingsActivity;
import com.forcetower.uefs.view.suggestion.SuggestionActivity;
import com.forcetower.uefs.vm.GradesViewModel;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

import static com.forcetower.uefs.ntf.NotificationCreator.GRADES_FRAGMENT;
import static com.forcetower.uefs.ntf.NotificationCreator.MESSAGES_FRAGMENT;
import static com.forcetower.uefs.util.PixelUtils.getPixelsFromDp;

public class ConnectedActivity extends UBaseActivity implements HasSupportFragmentInjector, NavigationController {
    public static final String NOTIFICATION_INTENT_EXTRA = "notification_intent_extra";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.pb_loading)
    ProgressBar pbLoading;
    @BindView(R.id.view_root)
    ViewGroup viewRoot;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private GradesViewModel gradesViewModel;

    private FragmentManager fragmentManager;
    @IdRes
    private int containerId;
    @StringRes
    private int titleText;
    private boolean afterLogin;
    private boolean showingTab;
    private int numberOfLoadings = 0;
    private int numberOfSemesters = -1;

    private boolean doubleBack;

    private boolean newScheduleLayout = true;

    public static void startActivity(Context context, boolean afterLogin) {
        Intent intent = new Intent(context, ConnectedActivity.class);
        intent.putExtra("after_login", afterLogin);
        Timber.d("Start connected activity!");
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_connected, savedInstanceState);
        setupToolbar();
        elevate();
        gradesViewModel = ViewModelProviders.of(this, viewModelFactory).get(GradesViewModel.class);
        gradesViewModel.getUNESLatestVersion().observe(this, this::onReceiveVersion);

        newScheduleLayout = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("new_schedule_layout", true);

        fragmentManager = getSupportFragmentManager();
        containerId = R.id.container;

        afterLogin = getIntent().getBooleanExtra("after_login", false);
        if (afterLogin && gradesViewModel.isAllGradesRunning()) {
            afterLogin = false;
        } else {
            if (!afterLogin && savedInstanceState != null) {
                afterLogin = savedInstanceState.getBoolean("after_login", false);
            }
        }
        Timber.d("After Login is %s", afterLogin);
        Timber.d("Is all grades completed? " + gradesViewModel.isAllGradesCompleted() + " is all grades running? " + gradesViewModel.isAllGradesRunning());

        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationOptionSelected);

        if (savedInstanceState == null) {
            setupAlarmManager();
            RefreshAlarmTrigger.enableBootComponent(this);
            setupShortcuts();

            boolean autoSync = ContentResolver.getMasterSyncAutomatically();
            boolean shown = PreferenceManager.getDefaultSharedPreferences(this)
                    .getBoolean(AutoSyncFragment.PREF_AUTO_SYNC_SHOWN, false);

            PreferenceManager.getDefaultSharedPreferences(this).edit()
                    .putBoolean("show_not_connected_notification", false).apply();

            String value = getIntent().getStringExtra(NOTIFICATION_INTENT_EXTRA);
            if (value == null) {
                Timber.d("Default open");
                if (!autoSync && !shown) {
                    navigateToAutoSync();
                } else {
                    navigateToSchedule();
                }
            } else {
                Timber.d("Action asks for: %s", value);
                if (value.equalsIgnoreCase(MESSAGES_FRAGMENT)) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_messages);
                    //navigateToMessages();
                } else if (value.equalsIgnoreCase(GRADES_FRAGMENT)) {
                    bottomNavigationView.setSelectedItemId(R.id.navigation_grades);
                    //navigateToGrades();
                } else {
                    navigateToSchedule();
                }
            }
        } else {
            titleText = savedInstanceState.getInt("title_text", R.string.title_schedule);
            showingTab = savedInstanceState.getBoolean("tab_showing", false);
            numberOfLoadings = savedInstanceState.getInt("number_of_loadings", 0);
            setTabShowing(showingTab);
            changeTitle(titleText);
        }

        afterLogin = afterLogin && !gradesViewModel.isAllGradesRunning();

        gradesViewModel.getAllSemestersGrade(afterLogin).observe(this, this::onReceiveGrades);
        gradesViewModel.getAllSemesters().observe(this, this::receiveListOfSemesters);
        gradesViewModel.getAccess().observe(this, this::accessObserver);
        if (afterLogin) {
            if (!gradesViewModel.isAllGradesCompleted())
                enableBottomLoading();
            else
                disableBottomLoading();
            //clearAllNotifications();
            afterLogin = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        newScheduleLayout = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("new_schedule_layout", true);
    }

    private void setupShortcuts() {
        if (!VersionUtils.isNougatMR1()) {
            return;
        }

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        Intent messages = new Intent(this, ConnectedActivity.class);
        messages.putExtra(NOTIFICATION_INTENT_EXTRA, MESSAGES_FRAGMENT);
        messages.setAction("android.intent.action.VIEW");
        messages.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent grades = new Intent(this, ConnectedActivity.class);
        grades.putExtra(NOTIFICATION_INTENT_EXTRA, GRADES_FRAGMENT);
        grades.setAction("android.intent.action.VIEW");
        grades.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        ShortcutInfo msgSrt = new ShortcutInfo.Builder(this, "messages")
                .setShortLabel(getString(R.string.title_messages))
                .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_message))
                .setIntent(messages)
                .build();

        ShortcutInfo grdSrt = new ShortcutInfo.Builder(this, "grades")
                .setShortLabel(getString(R.string.title_grades))
                .setIcon(Icon.createWithResource(this, R.drawable.ic_shortcut_school))
                .setIntent(grades)
                .build();

        //noinspection ConstantConditions
        shortcutManager.setDynamicShortcuts(Arrays.asList(msgSrt, grdSrt));
    }

    private void setupAlarmManager() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String strFrequency = preferences.getString("sync_frequency", "60");
        int frequency = 60;
        try {
            frequency = Integer.parseInt(strFrequency);
        } catch (Exception ignored) {}

        if (frequency != -1) RefreshAlarmTrigger.create(this, frequency);
        else                 RefreshAlarmTrigger.removeAlarm(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connected_top, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBack || !PreferenceManager.getDefaultSharedPreferences(this).getBoolean("double_back", false)) {
            super.onBackPressed();
            return;
        }

        this.doubleBack = true;
        Toast.makeText(this, R.string.press_back_twice, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBack = false, 2000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_settings) {
            SettingsActivity.startActivity(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void clearAllNotifications() {
        gradesViewModel.clearAllNotifications();
    }

    private void accessObserver(Access access) {
        if (access == null) {
            gradesViewModel.logout().observe(this, this::logoutObserver);
            Toast.makeText(this, R.string.disconnected, Toast.LENGTH_SHORT).show();
        }
    }

    private void logoutObserver(Resource<Integer> resource) {
        if (resource.status == Status.SUCCESS) {
            Timber.d("Finished erasing data");
            MainActivity.startActivity(this);
            finish();
        }
    }

    private boolean onNavigationOptionSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if      (id == R.id.navigation_home)        navigateToSchedule();
        else if (id == R.id.navigation_grades)      navigateToGrades();
        else if (id == R.id.navigation_messages)    navigateToMessages();
        else if (id == R.id.navigation_disciplines) navigateToDisciplines();
        else if (id == R.id.navigation_more)        navigateToMore();

        return true;
    }

    private void receiveListOfSemesters(List<Semester> semesters) {
        if (semesters == null) {
            Timber.d("Database returned a invalid list... Awkward");
            disableBottomLoading();
            return;
        }

        numberOfSemesters = semesters.size();
    }

    private void onReceiveGrades(Resource<Integer> resource) {
        if (resource == null) {
            Timber.d("Resource is null, maybe after login is false");
            disableBottomLoading();
            gradesViewModel.setAllGradesCompleted(true);
            return;
        }
        
        if (resource.status == Status.LOADING) {
            Timber.d("A new grade just finished!");
            numberOfLoadings++;
        } else if (resource.status == Status.ERROR) {
            Timber.d("A grade failed to download!");
            numberOfLoadings++;
        }

        if (numberOfSemesters != -1) {
            if (numberOfLoadings >= numberOfSemesters) {
                disableBottomLoading();
                gradesViewModel.setAllGradesCompleted(true);
                afterLogin = false;
            }
        }
    }

    @Override
    public void navigateToSchedule() {
        changeTitle(R.string.title_schedule);
        setTabShowing(false);
        if (newScheduleLayout) {
            changeFragment(new NewScheduleFragment());
        }
        else changeFragment(new ScheduleFragment());
    }

    @Override
    public void navigateToMessages() {
        changeTitle(R.string.title_messages);
        setTabShowing(false);
        changeFragment(new MessagesFragment());
    }

    @Override
    public void navigateToGrades() {
        changeTitle(R.string.title_grades);
        setTabShowing(true);
        changeFragment(new AllSemestersGradeFragment());
    }

    @Override
    public void navigateToDisciplines() {
        changeTitle(R.string.title_disciplines);
        setTabShowing(false);
        changeFragment(new DisciplinesFragment());
    }

    @Override
    public void navigateToMore() {
        changeTitle(R.string.title_more);
        setTabShowing(false);
        changeFragment(new ProfileFragment());
    }

    private void navigateToAutoSync() {
        changeTitle(R.string.title_auto_sync);
        setTabShowing(false);
        changeFragment(new AutoSyncFragment());
    }

    @Override
    public void navigateToCalendar() {
        changeTitle(R.string.title_calendar);
        setTabShowing(false);
        changeFragment(new CalendarFragment());
    }

    @Override
    public void navigateToBigTray() {
        changeTitle(R.string.title_big_tray);
        setTabShowing(false);
        changeFragment(new BigTrayFragment());
    }

    private void changeFragment(@NonNull Fragment fragment) {
        Fragment current = fragmentManager.findFragmentByTag(fragment.getClass().getSimpleName());
        if (current != null) fragment = current;

        fragmentManager.beginTransaction()
                .replace(containerId, fragment, fragment.getClass().getSimpleName())
                .commit();
    }

    private void changeTitle(@StringRes int idRes) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(idRes);
        }

        if (idRes == R.string.title_auto_sync) {
            appBarLayout.setVisibility(View.GONE);
            bottomNavigationView.setVisibility(View.GONE);
        } else {
            appBarLayout.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.VISIBLE);
        }

        titleText = idRes;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("after_login", afterLogin);
        outState.putInt("title_text", titleText);
        outState.putBoolean("tab_showing", showingTab);
        outState.putInt("number_of_loadings", numberOfLoadings);
        super.onSaveInstanceState(outState);
    }


    private void elevate() {
        if (VersionUtils.isLollipop()) {
            if (getSupportActionBar() != null)
                getSupportActionBar().setElevation(getPixelsFromDp(this, 10));

            bottomNavigationView.setElevation(getPixelsFromDp(this, 10));
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setTabShowing(boolean b) {
        if (!b) {
            //AnimUtils.fadeOutGone(this, tabLayout);
            tabLayout.setVisibility(View.GONE);
        }
        else AnimUtils.fadeIn(this, tabLayout);

        showingTab = b;
    }

    @Override
    public TabLayout getTabLayout() {
        return tabLayout;
    }

    @Override
    public void showNewScheduleError(Exception e) {
        newScheduleLayout = false;

        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean("new_schedule_layout", false)
                .apply();

        navigateToSchedule();

        Snackbar snackbar = Snackbar.make(viewRoot, getString(R.string.new_schedule_errors), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.send_error, v -> {
            SuggestionActivity.startActivity(this, e.getMessage(), e.getStackTrace());
            snackbar.dismiss();
        });
        snackbar.show();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    private void enableBottomLoading() {
        AnimUtils.fadeIn(this, pbLoading);
    }

    private void disableBottomLoading() {
        AnimUtils.fadeOutGone(this, pbLoading);
    }

    private void onReceiveVersion(ApiResponse<Version> versionResponse) {
        if (versionResponse == null) return;
        if (versionResponse.isSuccessful()) {
            Version version = versionResponse.body;
            if (version == null) {
                Timber.d("Received version is null");
                return;
            }

            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                int versionCode   = pInfo.versionCode;
                if (version.getVersionCode() > versionCode) {
                    Timber.d("There's an UNES update going on");

                    NotificationCreator.createNewVersionNotification(this, version);
                } else if (version.getVersionCode() == versionCode) {
                    Timber.d("UNES is up to date");
                } else {
                    Timber.d("This version is ahead of published version");
                }
            } catch (PackageManager.NameNotFoundException ignored) {}
        }
    }
}
