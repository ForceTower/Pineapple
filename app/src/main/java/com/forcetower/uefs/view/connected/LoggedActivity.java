package com.forcetower.uefs.view.connected;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IdRes;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.forcetower.uefs.BuildConfig;
import com.forcetower.uefs.Constants;
import com.forcetower.uefs.GooglePlayGamesInstance;
import com.forcetower.uefs.R;
import com.forcetower.uefs.alm.RefreshAlarmTrigger;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db.entity.Semester;
import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.Version;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.NetworkUtils;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.about.AboutActivity;
import com.forcetower.uefs.view.connected.fragments.AutoSyncFragment;
import com.forcetower.uefs.view.connected.fragments.ConnectedFragment;
import com.forcetower.uefs.view.login.MainActivity;
import com.forcetower.uefs.view.settings.SettingsActivity;
import com.forcetower.uefs.view.suggestion.SuggestionActivity;
import com.forcetower.uefs.vm.AchievementsViewModel;
import com.forcetower.uefs.vm.DownloadsViewModel;
import com.forcetower.uefs.vm.GradesViewModel;
import com.forcetower.uefs.vm.ProfileViewModel;
import com.forcetower.uefs.vm.ScheduleViewModel;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static com.forcetower.uefs.Constants.ENROLLMENT_CERTIFICATE_FILE_NAME;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.FRAGMENT_INTENT_EXTRA;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.GRADES_FRAGMENT;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.MESSAGES_FRAGMENT;

public class LoggedActivity extends UBaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        HasSupportFragmentInjector, ActivityController, GamesAccountController {
    private static final String SELECTED_NAV_DRAWER_ID = "selected_nav_drawer";
    public static final String BACKGROUND_IMAGE = "background_server_image";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.pb_global_progress)
    ProgressBar globalLoading;
    @BindView(R.id.root_coordinator)
    ViewGroup rootViewContent;

    private NavigationViews navViews;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;

    @StringRes
    private int titleText;
    private boolean doubleBack;

    private GradesViewModel gradesViewModel;
    private AchievementsViewModel achievementsViewModel;
    private DownloadsViewModel downloadsViewModel;

    private int numberOfLoadings = 0;
    private int numberOfSemesters = -1;
    private boolean afterLogin;
    @IdRes
    private int selectedNavId;

    private boolean disconnecting = false;
    private Profile latestProfile;
    private ActionBarDrawerToggle toggle;
    private boolean isHomeAsUp;

    public static void startActivity(Context context, boolean afterLogin) {
        Intent intent = new Intent(context, LoggedActivity.class);
        intent.putExtra("after_login", afterLogin);
        Timber.d("Start logged activity!");
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_logged, savedInstanceState);
        setSupportActionBar(toolbar);
        navViews = new NavigationViews();
        ButterKnife.bind(navViews, navigationView.getHeaderView(0));

        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        setupViewModels();
        setupIds();
        setupAfterLogin(savedInstanceState);
        setupNavigationItemColors();

        String backgroundImage = mPreferences.getString(BACKGROUND_IMAGE, Constants.BACKGROUND_IMAGE_DEFAULT);
        Picasso.with(this).load(backgroundImage).into(navViews.ivBackground);

        if (savedInstanceState != null) {
            onRestoreActivity(savedInstanceState);
        } else {
            onActivityCreated();
        }

        afterLogin = afterLogin && !gradesViewModel.isAllGradesRunning();
        loadGradesAndUnset();
    }

    private void loadGradesAndUnset() {
        gradesViewModel.getAllSemestersGrade(afterLogin).observe(this, this::onReceiveGrades);
        gradesViewModel.getAllSemesters().observe(this, this::receiveListOfSemesters);
        gradesViewModel.getAccess().observe(this, this::accessObserver);
        if (afterLogin) {
            if (!gradesViewModel.isAllGradesCompleted())
                enableBottomLoading();
            else
                disableBottomLoading();
            afterLogin = false;
        }
    }

    private void onActivityCreated() {
        setupAlarmManager();
        RefreshAlarmTrigger.enableBootComponent(this);
        setupShortcuts();
        setupFragmentStackListener();
        setupToolbarEvents();

        boolean autoSync = ContentResolver.getMasterSyncAutomatically();
        boolean shown = mPreferences.getBoolean(AutoSyncFragment.PREF_AUTO_SYNC_SHOWN, false);
        mPreferences.edit().putBoolean("show_not_connected_notification", false).apply();
        initiateActivity(autoSync, shown);
    }

    private void setupFragmentStackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            Timber.d("Size changed to %d", count);
            if (count == 0) {
                setHomeAsUp(false);
                return;
            }

            setHomeAsUp(true);

            /*
            FragmentManager.BackStackEntry entry = getSupportFragmentManager().getBackStackEntryAt(count - 1);
            if (entry != null) {
                String name = entry.getName();
                if (name != null && name.startsWith("other_arrow")) {
                    setHomeAsUp(true);
                } else {
                    setHomeAsUp(false);
                }
            }
            */
        });
    }

    public void setupToolbarEvents() {
        toolbar.setNavigationOnClickListener(v -> {
            if (drawer.isDrawerOpen(GravityCompat.START)){
                drawer.closeDrawer(GravityCompat.START);
            } else if (isHomeAsUp){
                onBackPressed();
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        });
    }

    public void setHomeAsUp(boolean isHomeAsUp){
        if (this.isHomeAsUp != isHomeAsUp) {
            this.isHomeAsUp = isHomeAsUp;

            if (isHomeAsUp) toggle.syncState();

            ValueAnimator anim = isHomeAsUp ? ValueAnimator.ofFloat(0, 1) : ValueAnimator.ofFloat(1, 0);
            anim.addUpdateListener(valueAnimator -> {
                float slideOffset = (Float) valueAnimator.getAnimatedValue();
                toggle.onDrawerSlide(drawer, slideOffset);
            });
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(400);
            anim.start();
        }
    }

    private void setupNavigationItemColors() {
        ColorStateList stateList = navigationView.getItemIconTintList();
        if (stateList == null) return;

        navigationView.setItemIconTintList(null);
        navigationView.setItemTextColor(null);

        defaultForAll(stateList);

        navigationView.getMenu().findItem(R.id.nav_schedule).getIcon()
                .setColorFilter(getResources().getColor(R.color.schedule_color), PorterDuff.Mode.SRC_IN);

        navigationView.getMenu().findItem(R.id.nav_messages).getIcon()
                .setColorFilter(getResources().getColor(R.color.messages_color), PorterDuff.Mode.SRC_IN);

        navigationView.getMenu().findItem(R.id.nav_grades).getIcon()
                .setColorFilter(getResources().getColor(R.color.grades_color), PorterDuff.Mode.SRC_IN);

        navigationView.getMenu().findItem(R.id.nav_disciplines).getIcon()
                .setColorFilter(getResources().getColor(R.color.disciplines_color), PorterDuff.Mode.SRC_IN);

        navigationView.getMenu().findItem(R.id.nav_calendar).getIcon()
                .setColorFilter(getResources().getColor(R.color.calendar_color), PorterDuff.Mode.SRC_IN);

        navigationView.getMenu().findItem(R.id.nav_enrollment_certificate).getIcon()
                .setColorFilter(getResources().getColor(R.color.enrollment_color), PorterDuff.Mode.SRC_IN);

        navigationView.getMenu().findItem(R.id.nav_big_tray).getIcon()
                .setColorFilter(getResources().getColor(R.color.big_tray_color), PorterDuff.Mode.SRC_IN);
    }

    private void defaultForAll(ColorStateList stateList) {
        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            MenuItem item = navigationView.getMenu().getItem(i);
             if (item == null || item.getIcon() == null) continue;

             if (VersionUtils.isLollipop()) item.getIcon().setTintList(stateList);
             else {
                 item.getIcon().setColorFilter(stateList.getDefaultColor(), PorterDuff.Mode.SRC_IN);
             }
        }
    }

    private void initiateActivity(boolean auto, boolean shown) {
        String value = getIntent().getStringExtra(FRAGMENT_INTENT_EXTRA);
        if (value == null) {
            Timber.d("Default open");
            if (!auto && !shown) {
                navigationController.navigateToAutoSync();
            } else {
                Bundle bundle = new Bundle();
                bundle.putString(FRAGMENT_INTENT_EXTRA, null);
                navigationController.navigateToMainContent(bundle);
            }
        } else {
            Timber.d("Action asks for: %s", value);
            Bundle bundle = new Bundle();
            bundle.putString(FRAGMENT_INTENT_EXTRA, value);
            navigationController.navigateToMainContent(bundle);
        }
    }

    private void onRestoreActivity(@NonNull Bundle savedInstanceState) {
        titleText = savedInstanceState.getInt("title_text", R.string.title_schedule);
        numberOfLoadings = savedInstanceState.getInt("number_of_loadings", 0);
        selectedNavId = savedInstanceState.getInt(SELECTED_NAV_DRAWER_ID);
        changeTitle(titleText);
        navigationView.setCheckedItem(selectedNavId);
    }

    private void setupAfterLogin(@Nullable Bundle savedInstanceState) {
        afterLogin = getIntent().getBooleanExtra("after_login", false);
        if (afterLogin && gradesViewModel.isAllGradesRunning()) {
            afterLogin = false;
        } else {
            if (!afterLogin && savedInstanceState != null) {
                afterLogin = savedInstanceState.getBoolean("after_login", false);
            }
        }
    }

    private void setupIds() {
        navigationView.getHeaderView(0).setOnClickListener(v -> {
            navigationController.navigateToProfile();
            drawer.closeDrawer(GravityCompat.START);
            navigationView.setCheckedItem(R.id.nav_profile);
            selectedNavId = R.id.nav_profile;
        });
    }

    private void setupViewModels() {
        gradesViewModel = ViewModelProviders.of(this, viewModelFactory).get(GradesViewModel.class);
        gradesViewModel.getUNESLatestVersion().observe(this, this::onReceiveVersion);

        ScheduleViewModel scheduleViewModel = ViewModelProviders.of(this, viewModelFactory).get(ScheduleViewModel.class);
        scheduleViewModel.getSingleLoadedLocation().observe(this, this::onReceiveSingleLocation);

        achievementsViewModel = ViewModelProviders.of(this, viewModelFactory).get(AchievementsViewModel.class);

        ProfileViewModel profileViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        profileViewModel.getProfileImage().observe(this, this::onReceiveProfileImage);
        profileViewModel.getProfile().observe(this, this::onReceiveProfile);

        downloadsViewModel = ViewModelProviders.of(this, viewModelFactory).get(DownloadsViewModel.class);
        downloadsViewModel.getDownloadCertificate().observe(this, this::onCertificateDownload);
    }

    private void onCertificateDownload(Resource<Integer> resource) {
        if (resource == null) return;
        if (resource.status == Status.LOADING) {
            //noinspection ConstantConditions
            Timber.d(getString(resource.data));
            globalLoading.setIndeterminate(true);
            AnimUtils.fadeIn(this, globalLoading);
        }
        else {
            AnimUtils.fadeOut(this, globalLoading);
            if (resource.status == Status.ERROR) {
                //noinspection ConstantConditions
                Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show();
            } else {
                Timber.d(getString(R.string.completed));
                openCertificatePdf(false);
            }
        }
    }

    private void openCertificatePdf(boolean clicked) {
        //noinspection ConstantConditions
        File file = new File(getCacheDir(), ENROLLMENT_CERTIFICATE_FILE_NAME);
        if (!file.exists()) {
            if (!clicked) {
                Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
            }
            else certificateDownload();
            return;
        }
        Intent target = new Intent(Intent.ACTION_VIEW);

        //noinspection ConstantConditions
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        Timber.d("Uri %s", uri);
        target.setDataAndType(uri,"application/pdf");
        target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(target, getString(R.string.open_file));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_pdf_reader, Toast.LENGTH_SHORT).show();
        }
    }

    private void certificateDownload() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.offline, Toast.LENGTH_SHORT).show();
            return;
        }

        downloadsViewModel.triggerDownloadCertificate();
        Toast.makeText(this, R.string.wait_until_download_finishes, Toast.LENGTH_SHORT).show();
    }

    private void onReceiveProfile(Profile profile) {
        latestProfile = profile;
        if (profile == null) return;

        navViews.tvNavTitle.setText(profile.getName());
        if (profile.getScore() >= 0) {
            navViews.tvNavSubtitle.setText(getString(R.string.student_score, profile.getScore()));
        } else {
            navViews.tvNavSubtitle.setText(R.string.no_score_message);
        }

        if (mPreferences.getBoolean("show_score", false)) {
            navViews.tvNavSubtitle.setVisibility(View.VISIBLE);
        } else {
            navViews.tvNavSubtitle.setVisibility(View.GONE);
        }
    }

    private void onReceiveProfileImage(Bitmap bitmap) {
        if (bitmap == null) {
            Timber.d("No image set so far");
            AnimUtils.fadeIn(this, navViews.ivNavUserImagePlaceHolder);
            AnimUtils.fadeOut(this, navViews.ivNavUserImage);
            return;
        }

        navViews.ivNavUserImage.setImageBitmap(bitmap);
        AnimUtils.fadeIn(this, navViews.ivNavUserImage);
        AnimUtils.fadeOut(this, navViews.ivNavUserImagePlaceHolder);
    }

    private void onReceiveVersion(ApiResponse<Version> versionResponse) {
        if (versionResponse == null) return;
        if (versionResponse.isSuccessful()) {
            Version version = versionResponse.body;
            if (version == null) {
                Timber.d("Received version is null");
                return;
            }

            String backgroundImage = version.getBackgroundImage();
            if (backgroundImage != null) {
                mPreferences.edit().putString(BACKGROUND_IMAGE, backgroundImage).apply();
                Picasso.with(this).load(backgroundImage).into(navViews.ivBackground);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (navViews != null) {
            if (mPreferences.getBoolean("show_score", false)) {
                navViews.tvNavSubtitle.setVisibility(View.VISIBLE);
            } else {
                navViews.tvNavSubtitle.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        boolean ignoreCheckable = false;
        if (id != selectedNavId) {
            if (id == R.id.nav_profile) {
                navigationController.navigateToProfile();
                tabLayout.setVisibility(View.GONE);
            } else if (id == R.id.nav_game) {
                navigationController.navigateToUNESGame();
                tabLayout.setVisibility(View.GONE);
            } else if (id == R.id.nav_schedule) {
                navigationController.navigateToSchedule();
                tabLayout.setVisibility(View.GONE);
            } else if (id == R.id.nav_messages) {
                navigationController.navigateToMessages();
                tabLayout.setVisibility(View.GONE);
            } else if (id == R.id.nav_grades) {
                navigationController.navigateToGrades();
            } else if (id == R.id.nav_disciplines) {
                navigationController.navigateToDisciplines();
                tabLayout.setVisibility(View.GONE);
            } else if (id == R.id.nav_calendar) {
                navigationController.navigateToCalendar();
                tabLayout.setVisibility(View.GONE);
            } else if (id == R.id.nav_big_tray) {
                if ((latestProfile != null && latestProfile.getName().equalsIgnoreCase("jpssena")) || BuildConfig.DEBUG) {
                    navigationController.navigateToBigTray();
                    ignoreCheckable = true;
                } else {
                    NetworkUtils.openLink(this, "http://bit.ly/bandejaouefs");
                }
            } else if (id == R.id.nav_settings) {
                goToSettings();
            } else if (id == R.id.nav_logout) {
                performLogout();
            } else if (id == R.id.nav_feedback) {
                goToFeedback();
            } else if (id == R.id.nav_about) {
                goToAbout();
            } else if (id == R.id.nav_enrollment_certificate) {
                openCertificatePdf(true);
            }

            if (item.isCheckable() || ignoreCheckable) selectedNavId = id;
            if (ignoreCheckable) navigationView.setCheckedItem(id);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void performLogout() {
        disconnecting = true;
        RefreshAlarmTrigger.disableBootComponent(this);
        RefreshAlarmTrigger.removeAlarm(this);
        gradesViewModel.logout().observe(this, this::logoutObserver);
    }

    private void goToFeedback() {
        SuggestionActivity.startActivity(this);
    }

    private void goToAbout() {
        AboutActivity.startActivity(this);
    }

    private void goToSettings() {
        SettingsActivity.startActivity(this);
    }

    private void setupAlarmManager() {
        String strFrequency = mPreferences.getString("sync_frequency", "60");
        int frequency = 60;
        try {
            frequency = Integer.parseInt(strFrequency);
        } catch (Exception ignored) {}

        if (frequency != -1) RefreshAlarmTrigger.create(this, frequency);
        else                 RefreshAlarmTrigger.removeAlarm(this);
    }

    private void setupShortcuts() {
        if (!VersionUtils.isNougatMR1()) {
            return;
        }

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        Intent messages = new Intent(this, LoggedActivity.class);
        messages.putExtra(FRAGMENT_INTENT_EXTRA, MESSAGES_FRAGMENT);
        messages.setAction("android.intent.action.VIEW");
        messages.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

        Intent grades = new Intent(this, LoggedActivity.class);
        grades.putExtra(FRAGMENT_INTENT_EXTRA, GRADES_FRAGMENT);
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

    @MainThread
    @Override
    public void changeTitle(@StringRes int idRes) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(idRes);
        }
        titleText = idRes;
    }

    @Override
    @MainThread
    public void selectItemFromNavigation(@IdRes int id) {
        int idRes = -1;

        if      (id == R.id.navigation_home)        idRes = R.id.nav_schedule;
        else if (id == R.id.navigation_grades)      idRes = R.id.nav_grades;
        else if (id == R.id.navigation_messages)    idRes = R.id.nav_messages;
        else if (id == R.id.navigation_disciplines) idRes = R.id.nav_disciplines;
        else if (id == R.id.navigation_calendar)    idRes = R.id.nav_calendar;
        if (idRes != -1) {
            selectedNavId = idRes;
            navigationView.setCheckedItem(idRes);
        }
    }

    @Override
    public void onProfileImageChanged(Bitmap bitmap) {
        onReceiveProfileImage(bitmap);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("after_login", afterLogin);
        outState.putInt("title_text", titleText);
        outState.putInt("number_of_loadings", numberOfLoadings);
        outState.putInt(SELECTED_NAV_DRAWER_ID, selectedNavId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
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

    private void receiveListOfSemesters(List<Semester> semesters) {
        if (semesters == null) {
            Timber.d("Database returned a invalid list... Awkward");
            disableBottomLoading();
            return;
        }

        numberOfSemesters = semesters.size();
    }

    private void accessObserver(Access access) {
        if (access == null && !disconnecting) {
            gradesViewModel.logout().observe(this, this::logoutObserver);
            Toast.makeText(this, R.string.disconnected, Toast.LENGTH_SHORT).show();
        } else if (access != null){
            mPlayGamesInstance.changePlayerName(access.getUsername());
        }
    }

    private void logoutObserver(Resource<Integer> resource) {
        if (resource.status == Status.SUCCESS) {
            Timber.d("Finished erasing data");
            MainActivity.startActivity(this);
        }
    }

    private void onReceiveSingleLocation(DisciplineClassLocation location) {
        if (location != null) {
            mPreferences.edit().putBoolean("new_schedule_user_ready", true).apply();
            Timber.d("New schedule can be enabled because user already sync'ed stuff");
        } else {
            mPreferences.edit().putBoolean("new_schedule_user_ready", false).apply();
            Timber.d("User needs to sync stuff before using the new layout");
        }
    }

    private void enableBottomLoading() {
        AnimUtils.fadeIn(this, globalLoading);
    }

    private void disableBottomLoading() {
        AnimUtils.fadeOutGone(this, globalLoading);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBack || !mPreferences.getBoolean("double_back", false)) {
                super.onBackPressed();
                return;
            }

            this.doubleBack = true;
            Toast.makeText(this, R.string.press_back_twice, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBack = false, 2000);
        }
    }

    @Override
    public void checkAchievements() {
        super.checkAchievements();
        achievementsViewModel.checkAchievements().observe(this, this::onAchievementsUpdate);
        Timber.d("This where called");
    }

    private void onAchievementsUpdate(HashMap<Integer, Integer> integers) {
        if (!mPlayGamesInstance.isSignedIn() || integers == null || integers.isEmpty()) {
            Timber.d("Returned because %s %s", !mPlayGamesInstance.isSignedIn(), integers);
            return;
        }

        for (int id : integers.keySet()) {
            int value = integers.get(id);
            if (value == -1)
                unlockAchievements(getString(id), mPlayGamesInstance);
            else
                publishAchievementProgress(getString(id), value, mPlayGamesInstance);
        }
    }

    @Override
    public TabLayout getTabLayout() {
        return tabLayout;
    }

    @Override
    public NavigationController getNavigationController() {
        return navigationController;
    }

    @Override
    public void showNewScheduleError(Exception e) {
        mPreferences.edit().putBoolean("new_schedule_layout", false).apply();

        navigationController.navigateToSchedule();

        Snackbar snackbar = Snackbar.make(rootViewContent, getString(R.string.new_schedule_errors), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.send_error, v -> {
            SuggestionActivity.startActivity(this, e.getMessage(), e.getStackTrace());
            snackbar.dismiss();
        });
        snackbar.show();
    }

    @Override
    public void navigateToDisciplineDetails(int groupUid, int disciplineUid) {
        navigationView.setCheckedItem(R.id.nav_disciplines);
        selectedNavId = -1;
        navigationController.navigateToDisciplineDetails(groupUid, disciplineUid);
    }

    @Override
    public void navigateToDisciplineClasses(int groupId) {
        navigationController.navigateToDisciplineClasses(groupId);
    }

    @Override
    public GooglePlayGamesInstance getPlayGamesInstance() {
        return mPlayGamesInstance;
    }

    @Override
    public void unlockAchievements(@NonNull String achievement, @NonNull GooglePlayGamesInstance playGamesInstance) {
        super.unlockAchievements(achievement, playGamesInstance);
    }

    class NavigationViews {
        @BindView(R.id.iv_nav_image)
        CircleImageView ivNavUserImage;
        @BindView(R.id.iv_nav_image_placeholder)
        CircleImageView ivNavUserImagePlaceHolder;
        @BindView(R.id.tv_nav_title)
        TextView tvNavTitle;
        @BindView(R.id.tv_nav_subtitle)
        TextView tvNavSubtitle;
        @BindView(R.id.iv_background)
        ImageView ivBackground;
    }
}
