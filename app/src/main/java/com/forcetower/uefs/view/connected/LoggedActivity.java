package com.forcetower.uefs.view.connected;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.ColorStateList;
import androidx.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.IdRes;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.google.android.material.appbar.AppBarLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.BuildConfig;
import com.forcetower.uefs.GooglePlayGamesInstance;
import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ActivityLoggedBinding;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.db_service.entity.Version;
import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.SagresDocuments;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.NetworkUtils;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.util.WordUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.about.AboutActivity;
import com.forcetower.uefs.view.login.MainActivity;
import com.forcetower.uefs.view.settings.SettingsActivity;
import com.forcetower.uefs.vm.base.DisciplinesViewModel;
import com.forcetower.uefs.vm.base.DownloadsViewModel;
import com.forcetower.uefs.vm.base.GradesViewModel;
import com.forcetower.uefs.vm.base.ProfileViewModel;
import com.forcetower.uefs.vm.base.ScheduleViewModel;
import com.forcetower.uefs.vm.google.AchievementsViewModel;
import com.forcetower.uefs.vm.universe.UAccountViewModel;
import com.forcetower.uefs.work.grades.DownloadGradesWorker;
import com.forcetower.uefs.work.sync.SyncWorkerUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import javax.inject.Inject;

import androidx.work.WorkManager;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

import static com.forcetower.uefs.Constants.ENROLLMENT_CERTIFICATE_FILE_NAME;
import static com.forcetower.uefs.Constants.FLOWCHART_FILE_NAME;
import static com.forcetower.uefs.Constants.SCHOLAR_HISTORY_FILE_NAME;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.FRAGMENT_INTENT_EXTRA;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.GRADES_FRAGMENT;
import static com.forcetower.uefs.view.connected.fragments.ConnectedFragment.MESSAGES_FRAGMENT_SAGRES;

public class LoggedActivity extends UBaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        HasSupportFragmentInjector, ActivityController, GamesAccountController {
    private static final String SELECTED_NAV_DRAWER_ID = "selected_nav_drawer";
    public static final String BACKGROUND_IMAGE = "background_server_image";

    private ActivityLoggedBinding binding;

    private NavigationViews navViews;
    private NavigationCustomActionViews downloadCert;
    private NavigationCustomActionViews downloadFlow;
    private NavigationCustomActionViews downloadHist;
    private double scoreCalc = -1;
    private boolean alternateScore = false;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    NavigationController navigationController;
    @Inject
    AppExecutors executors;

    @StringRes
    private int titleText;
    private boolean doubleBack;

    private GradesViewModel gradesViewModel;
    private AchievementsViewModel achievementsViewModel;
    private DownloadsViewModel downloadsViewModel;
    private UAccountViewModel uAccountViewModel;

    @IdRes
    private int selectedNavId;

    private boolean disconnecting = false;
    private ActionBarDrawerToggle toggle;
    private boolean isHomeAsUp;
    private boolean isPDFResultShown;
    private boolean showedOnSession;
    private Toolbar toolbar;
    private TabLayout tabLayout;

    public static void startActivity(Context context, boolean afterLogin) {
        Intent intent = new Intent(context, LoggedActivity.class);
        intent.putExtra("after_login", afterLogin);
        Timber.d("Start logged activity!");
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_logged);
        toolbar = binding.appLayout.contentLogged.incToolbar.toolbar;
        tabLayout = binding.appLayout.contentLogged.incToolbar.tabLayout;
        setSupportActionBar(toolbar);

        navViews = new NavigationViews();
        downloadCert = new NavigationCustomActionViews();
        downloadFlow = new NavigationCustomActionViews();
        downloadHist = new NavigationCustomActionViews();

        navViews.bindTo(binding.navView.getHeaderView(0));
        downloadCert.bindTo(binding.navView.getMenu().findItem(R.id.nav_enrollment_certificate).getActionView());
        downloadFlow.bindTo(binding.navView.getMenu().findItem(R.id.nav_flowchart_certificate).getActionView());
        downloadHist.bindTo(binding.navView.getMenu().findItem(R.id.nav_history_certificate).getActionView());

        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(this);

        setupViewModels();
        setupIds();
        setupNavigationItemColors();
        setupFragmentStackListener();
        setupToolbarEvents();
        setupActionListeners();

        if (savedInstanceState != null) {
            onRestoreActivity(savedInstanceState);
        } else {
            onActivityCreated();
        }
    }

    private void setupActionListeners() {
        downloadCert.vgRoot.setOnClickListener(v -> {
            if (downloadsViewModel.isBusy()) return;
            certificateDownload();
            AnimUtils.fadeOut(this, downloadCert.ivAction);
            AnimUtils.fadeIn(this, downloadCert.pbAction);
        });

        downloadFlow.vgRoot.setOnClickListener(v -> {
            if (downloadsViewModel.isBusy()) return;
            flowchartDownload();
            AnimUtils.fadeOut(this, downloadFlow.ivAction);
            AnimUtils.fadeIn(this, downloadFlow.pbAction);
        });

        downloadHist.vgRoot.setOnClickListener(v -> {
            if (downloadsViewModel.isBusy()) return;
            schoolHistoryDownload();
            AnimUtils.fadeOut(this, downloadHist.ivAction);
            AnimUtils.fadeIn(this, downloadHist.pbAction);
        });
    }

    private void onActivityCreated() {
        setupShortcuts();
        boolean afterLogin = getIntent().getBooleanExtra("after_login", false);
        mPreferences.edit().putBoolean("show_not_connected_notification", true).apply();
        initiateActivity();

        if (afterLogin) {
            DownloadGradesWorker.createWorker();
            Toast.makeText(this, R.string.downloading_your_grades, Toast.LENGTH_SHORT).show();
        }
    }

    private void setupFragmentStackListener() {
        int c = getSupportFragmentManager().getBackStackEntryCount();
        if (c == 0) {
            setHomeAsUp(false);
        } else {
            setHomeAsUp(true);
        }

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            Timber.d("Size changed to %d", count);
            if (count == 0) {
                setHomeAsUp(false);
                return;
            }

            setHomeAsUp(true);
        });
    }

    public void setupToolbarEvents() {
        toolbar.setNavigationOnClickListener(v -> {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
                binding.drawerLayout.closeDrawer(GravityCompat.START);
            } else if (isHomeAsUp){
                onBackPressed();
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.START);
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
                toggle.onDrawerSlide(binding.drawerLayout, slideOffset);
            });
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(400);
            anim.start();
        }
    }

    private void setupNavigationItemColors() {
        ColorStateList stateList = binding.navView.getItemIconTintList();
        if (stateList == null) return;

        binding.navView.setItemIconTintList(null);
        binding.navView.setItemTextColor(null);

        defaultForAll(stateList);

        binding.navView.getMenu().findItem(R.id.nav_schedule).getIcon()
                .setColorFilter(getResources().getColor(R.color.schedule_color), PorterDuff.Mode.SRC_IN);

        binding.navView.getMenu().findItem(R.id.nav_messages).getIcon()
                .setColorFilter(getResources().getColor(R.color.messages_color), PorterDuff.Mode.SRC_IN);

        binding.navView.getMenu().findItem(R.id.nav_grades).getIcon()
                .setColorFilter(getResources().getColor(R.color.grades_color), PorterDuff.Mode.SRC_IN);

        binding.navView.getMenu().findItem(R.id.nav_disciplines).getIcon()
                .setColorFilter(getResources().getColor(R.color.disciplines_color), PorterDuff.Mode.SRC_IN);

        binding.navView.getMenu().findItem(R.id.nav_calendar).getIcon()
                .setColorFilter(getResources().getColor(R.color.calendar_color), PorterDuff.Mode.SRC_IN);

        binding.navView.getMenu().findItem(R.id.nav_enrollment_certificate).getIcon()
                .setColorFilter(getResources().getColor(R.color.enrollment_color), PorterDuff.Mode.SRC_IN);

        binding.navView.getMenu().findItem(R.id.nav_flowchart_certificate).getIcon()
                .setColorFilter(getResources().getColor(R.color.flowchart_color), PorterDuff.Mode.SRC_IN);

        binding.navView.getMenu().findItem(R.id.nav_history_certificate).getIcon()
                .setColorFilter(getResources().getColor(R.color.school_history_color), PorterDuff.Mode.SRC_IN);

//        binding.navView.getMenu().findItem(R.id.nav_big_tray).getIcon()
//                .setColorFilter(getResources().getColor(R.color.big_tray_color), PorterDuff.Mode.SRC_IN);
    }

    private void defaultForAll(ColorStateList stateList) {
        for (int i = 0; i < binding.navView.getMenu().size(); i++) {
            MenuItem item = binding.navView.getMenu().getItem(i);
             if (item == null || item.getIcon() == null) continue;

             if (VersionUtils.isLollipop()) item.getIcon().setTintList(stateList);
             else {
                 item.getIcon().setColorFilter(stateList.getDefaultColor(), PorterDuff.Mode.SRC_IN);
             }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Timber.d("Clicked on menu options");
        return super.onOptionsItemSelected(item);
    }

    private void initiateActivity() {
        String value = getIntent().getStringExtra(FRAGMENT_INTENT_EXTRA);
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_INTENT_EXTRA, value);
        navigationController.navigateToMainContent(bundle);
    }

    private void onRestoreActivity(@NonNull Bundle savedInstanceState) {
        titleText = savedInstanceState.getInt("title_text", R.string.title_schedule);
        selectedNavId = savedInstanceState.getInt(SELECTED_NAV_DRAWER_ID);
        isPDFResultShown = savedInstanceState.getBoolean("pdf_result_shown", false);
        showedOnSession = savedInstanceState.getBoolean("showed_on_session", false);
        changeTitle(titleText);
        binding.navView.setCheckedItem(selectedNavId);
    }

    private void setupIds() {
        binding.navView.getHeaderView(0).setOnClickListener(v -> {
            navigationController.navigateToProfile();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            binding.navView.setCheckedItem(R.id.nav_profile);
            selectedNavId = R.id.nav_profile;
        });
    }

    private void setupViewModels() {
        gradesViewModel = ViewModelProviders.of(this, viewModelFactory).get(GradesViewModel.class);
        gradesViewModel.getUNESLatestVersion().observe(this, this::onReceiveVersion);
        gradesViewModel.getAccess().observe(this, this::accessObserver);

        ScheduleViewModel scheduleViewModel = ViewModelProviders.of(this, viewModelFactory).get(ScheduleViewModel.class);
        scheduleViewModel.getSingleLoadedLocation().observe(this, this::onReceiveSingleLocation);

        achievementsViewModel = ViewModelProviders.of(this, viewModelFactory).get(AchievementsViewModel.class);

        ProfileViewModel profileViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        profileViewModel.getProfileImage().observe(this, this::onReceiveProfileImage);
        profileViewModel.getProfile().observe(this, this::onReceiveProfile);

        downloadsViewModel = ViewModelProviders.of(this, viewModelFactory).get(DownloadsViewModel.class);
        downloadsViewModel.getDownloadCertificate().observe(this, this::onCertificateDownload);
        downloadsViewModel.getDownloadFlowchart().observe(this, this::onFlowchartDownload);
        downloadsViewModel.getDownloadHistory().observe(this, this::onHistoryDownload);

        uAccountViewModel = ViewModelProviders.of(this, viewModelFactory).get(UAccountViewModel.class);

        DisciplinesViewModel disciplinesViewModel = ViewModelProviders.of(this, viewModelFactory).get(DisciplinesViewModel.class);
        disciplinesViewModel.getScore().observe(this, this::onScoreCalculated);
    }

    private void onScoreCalculated(Double value) {
        if (value != null) {
            this.scoreCalc = value;
            if (scoreCalc >= 0 && alternateScore) {
                navViews.tvNavSubtitle.setText(getString(R.string.calculated_score, scoreCalc));
            }
        }
    }

    private void onCertificateDownload(Resource<Integer> resource) {
        if (resource == null) return;
        if (resource.status == Status.LOADING) {
            //noinspection ConstantConditions
            Timber.d(getString(resource.data));
            downloadCert.pbAction.setIndeterminate(true);
            downloadCert.ivAction.setVisibility(View.INVISIBLE);
            AnimUtils.fadeIn(this, downloadCert.pbAction);
        }
        else {
            AnimUtils.fadeOut(this, downloadCert.pbAction);
            downloadCert.ivAction.setVisibility(View.VISIBLE);
            if (resource.status == Status.ERROR) {
                //noinspection ConstantConditions
                Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show();
            } else {
                Timber.d(getString(R.string.completed));
                if (!isPDFResultShown) openCertificatePdf(false, SagresDocuments.ENROLLMENT_CERTIFICATE);
            }
        }
    }

    private void onFlowchartDownload(Resource<Integer> resource) {
        if (resource == null) return;
        if (resource.status == Status.LOADING) {
            //noinspection ConstantConditions
            Timber.d(getString(resource.data));
            //globalLoading.setIndeterminate(true);
            downloadFlow.pbAction.setIndeterminate(true);
            downloadFlow.ivAction.setVisibility(View.INVISIBLE);
            AnimUtils.fadeIn(this, downloadFlow.pbAction);
        }
        else {
            AnimUtils.fadeOut(this, downloadFlow.pbAction);
            downloadFlow.ivAction.setVisibility(View.VISIBLE);
            if (resource.status == Status.ERROR) {
                //noinspection ConstantConditions
                Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show();
            } else {
                Timber.d(getString(R.string.completed));
                if (!isPDFResultShown) openCertificatePdf(false, SagresDocuments.FLOWCHART);
            }
        }
    }

    private void onHistoryDownload(Resource<Integer> resource) {
        if (resource == null) return;
        if (resource.status == Status.LOADING) {
            //noinspection ConstantConditions
            Timber.d(getString(resource.data));
            //globalLoading.setIndeterminate(true);
            downloadHist.pbAction.setIndeterminate(true);
            downloadHist.ivAction.setVisibility(View.INVISIBLE);
            AnimUtils.fadeIn(this, downloadHist.pbAction);
        }
        else {
            AnimUtils.fadeOut(this, downloadHist.pbAction);
            downloadHist.ivAction.setVisibility(View.VISIBLE);
            if (resource.status == Status.ERROR) {
                //noinspection ConstantConditions
                Toast.makeText(this, resource.data, Toast.LENGTH_SHORT).show();
            } else {
                Timber.d(getString(R.string.completed));
                if (!isPDFResultShown) openCertificatePdf(false, SagresDocuments.SCHOLAR_HISTORY);
            }
        }
    }

    private void openCertificatePdf(boolean clicked, SagresDocuments option) {
        //noinspection ConstantConditions
        File file;
        if (option == SagresDocuments.FLOWCHART) {
            file = new File(getCacheDir(), FLOWCHART_FILE_NAME);
        } else if (option == SagresDocuments.SCHOLAR_HISTORY) {
            file = new File(getCacheDir(), SCHOLAR_HISTORY_FILE_NAME);
        } else {
            file = new File(getCacheDir(), ENROLLMENT_CERTIFICATE_FILE_NAME);
        }

        if (!file.exists()) {
            if (!clicked) {
                Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
            }
            else {
                if (option == SagresDocuments.ENROLLMENT_CERTIFICATE)
                    certificateDownload();
                else if (option == SagresDocuments.FLOWCHART)
                    flowchartDownload();
                else if (option == SagresDocuments.SCHOLAR_HISTORY)
                    schoolHistoryDownload();
            }
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
            isPDFResultShown = true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.no_pdf_reader, Toast.LENGTH_SHORT).show();
        }
    }

    private void schoolHistoryDownload() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.offline, Toast.LENGTH_SHORT).show();
            return;
        }

        if (downloadsViewModel.isBusy()){
            Toast.makeText(this, R.string.downloader_is_busy, Toast.LENGTH_SHORT).show();
            return;
        }

        isPDFResultShown = false;
        downloadsViewModel.triggerDownloadSchoolHistory();
        Toast.makeText(this, R.string.wait_until_download_finishes, Toast.LENGTH_SHORT).show();
    }

    private void certificateDownload() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.offline, Toast.LENGTH_SHORT).show();
            return;
        }

        if (downloadsViewModel.isBusy()){
            Toast.makeText(this, R.string.downloader_is_busy, Toast.LENGTH_SHORT).show();
            return;
        }

        isPDFResultShown = false;

        downloadsViewModel.triggerDownloadCertificate();
        Toast.makeText(this, R.string.wait_until_download_finishes, Toast.LENGTH_SHORT).show();
    }

    private void flowchartDownload() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, R.string.offline, Toast.LENGTH_SHORT).show();
            return;
        }

        if (downloadsViewModel.isBusy()){
            Toast.makeText(this, R.string.downloader_is_busy, Toast.LENGTH_SHORT).show();
            return;
        }

        isPDFResultShown = false;

        downloadsViewModel.triggerDownloadFlowchart();
        Toast.makeText(this, R.string.wait_until_download_finishes, Toast.LENGTH_SHORT).show();
    }

    private void onReceiveProfile(Profile profile) {
        if (profile == null) return;

        navViews.tvNavTitle.setText(profile.getName());
        if (profile.getScore() >= 0) {
            navViews.tvNavSubtitle.setText(getString(R.string.student_score, profile.getScore()));
        } else {
            alternateScore = true;
            if (scoreCalc >= 0) {
                navViews.tvNavSubtitle.setText(getString(R.string.calculated_score, scoreCalc));
            } else {
                navViews.tvNavSubtitle.setText(R.string.no_score_message);
            }
        }

        if (mPreferences.getBoolean("show_score", false)) {
            navViews.tvNavSubtitle.setVisibility(View.VISIBLE);
        } else {
            navViews.tvNavSubtitle.setVisibility(View.GONE);
        }

        Timber.d("User Course: %s", profile.getCourse());
        if ((profile.getCourse() == null || profile.getCourse().isEmpty()) && !showedOnSession) {
            showedOnSession = true;
            Snackbar snack = Snackbar.make(binding.appLayout.contentLogged.drawerContainer, R.string.setup_your_course, Snackbar.LENGTH_LONG);
            snack.setAction(R.string.course_answer, v -> {
                navigationController.navigateToSelectCourse();
                snack.dismiss();
            });
            snack.show();
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
            }

            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
                int versionCode   = pInfo.versionCode;
                if (version.getCode() > versionCode) {
                    Timber.d("There's an UNES update going on");
                    NotificationCreator.createNewVersionNotification(this, version);
                } else if (version.getCode() == versionCode) {
                    Timber.d("UNES is up to date");
                } else {
                    Timber.d("This version is ahead of published version");
                }

                if (version.getDisableCode() >= versionCode) {
                    Timber.d("This version is really outdated");
                    clearBackStack();
                    navigationController.navigateToOutdatedVersion();
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

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
        if (id != selectedNavId) {
            if (id == R.id.nav_profile) {
                clearBackStack();
                navigationController.navigateToProfile();
                tabLayout.setVisibility(View.GONE);
            } else if (id == R.id.nav_game) {
                clearBackStack();
                navigationController.navigateToUNESGame();
                tabLayout.setVisibility(View.GONE);
            } else if (id == R.id.nav_schedule) {
                clearBackStack();
                navigationController.navigateToSchedule();
                tabLayout.setVisibility(View.GONE);
            } else if (id == R.id.nav_messages) {
                clearBackStack();
                navigationController.navigateToMessages();
                tabLayout.setVisibility(View.VISIBLE);
            } else if (id == R.id.nav_grades) {
                clearBackStack();
                navigationController.navigateToGrades();
            } else if (id == R.id.nav_disciplines) {
                clearBackStack();
                navigationController.navigateToDisciplines();
                tabLayout.setVisibility(View.GONE);
            } else if (id == R.id.nav_calendar) {
                clearBackStack();
                navigationController.navigateToCalendar();
                tabLayout.setVisibility(View.GONE);
            }/*else if (id == R.id.nav_big_tray) {
                clearBackStack();
                navigationController.navigateToBigTray();
                tabLayout.setVisibility(View.GONE);
            }*/else if (id == R.id.nav_settings) {
                goToSettings();
            } else if (id == R.id.nav_logout) {
                performLogout();
            } else if (id == R.id.nav_feedback) {
                clearBackStack();
                navigationController.navigateToSuggestionFragment(null, null);
            } else if (id == R.id.nav_about) {
                goToAbout();
            } else if (id == R.id.nav_enrollment_certificate) {
                openCertificatePdf(true, SagresDocuments.ENROLLMENT_CERTIFICATE);
            } else if (id == R.id.nav_flowchart_certificate) {
                openCertificatePdf(true, SagresDocuments.FLOWCHART);
            } else if (id == R.id.nav_history_certificate) {
                openCertificatePdf(true, SagresDocuments.SCHOLAR_HISTORY);
            } else if (id == R.id.nav_events) {
                clearBackStack();
                tabLayout.setVisibility(View.GONE);
                navigationController.navigateToEvents();
            } else if (id == R.id.nav_reminders) {
                clearBackStack();
                navigationController.navigateToReminders();
                tabLayout.setVisibility(View.GONE);
            }

            if (item.isCheckable()) selectedNavId = id;
        }

        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void clearBackStack() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++)
            getSupportFragmentManager().popBackStack();
    }

    private void performLogout() {
        disconnecting = true;
        SyncWorkerUtils.disableWorker(this);
        DownloadGradesWorker.disableWorkers();
        WorkManager.getInstance().cancelAllWork();
        gradesViewModel.logout().observe(this, this::logoutObserver);
    }

    private void goToAbout() {
        AboutActivity.startActivity(this);
    }

    private void goToSettings() {
        SettingsActivity.startActivity(this);
    }

    private void setupShortcuts() {
        if (!VersionUtils.isNougatMR1()) {
            return;
        }

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        Intent messages = new Intent(this, LoggedActivity.class);
        messages.putExtra(FRAGMENT_INTENT_EXTRA, MESSAGES_FRAGMENT_SAGRES);
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
            binding.navView.setCheckedItem(idRes);
        }
    }

    @Override
    public void onProfileImageChanged(Bitmap bitmap) {
        onReceiveProfileImage(bitmap);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("pdf_result_shown", isPDFResultShown);
        outState.putInt("title_text", titleText);
        outState.putInt(SELECTED_NAV_DRAWER_ID, selectedNavId);
        outState.putBoolean("showed_on_session", showedOnSession);
        super.onSaveInstanceState(outState);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }

    private void accessObserver(Access access) {
        if (access == null && !disconnecting) {
            Timber.d("Access got invalidated");
        } else if (access != null) {
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
        AnimUtils.fadeIn(this, binding.appLayout.contentLogged.pbGlobalProgress);
    }

    private void disableBottomLoading() {
        AnimUtils.fadeOutGone(this, binding.appLayout.contentLogged.pbGlobalProgress);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();
            if (doubleBack || !mPreferences.getBoolean("double_back", false) || count != 0) {
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

        Snackbar snackbar = Snackbar.make(binding.appLayout.rootCoordinator, getString(R.string.new_schedule_errors), Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.send_error, v -> {
            navigationController.navigateToSuggestionFragment(e.getMessage(), WordUtils.buildFromStackTrace(e.getStackTrace()));
            snackbar.dismiss();
        });
        snackbar.show();
    }

    @Override
    public void navigateToDisciplineDetails(int groupUid, int disciplineUid) {
        binding.navView.setCheckedItem(R.id.nav_disciplines);
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

    @Override
    public void disableDrawer() {
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }

        toggle.setDrawerIndicatorEnabled(false);

    }

    class NavigationViews {
        CircleImageView ivNavUserImage;
        CircleImageView ivNavUserImagePlaceHolder;
        TextView tvNavTitle;
        TextView tvNavSubtitle;

        private void bindTo(View headerView) {
            ivNavUserImage = headerView.findViewById(R.id.image);
            ivNavUserImagePlaceHolder = headerView.findViewById(R.id.image_placeholder);
            tvNavTitle = headerView.findViewById(R.id.name);
            tvNavSubtitle = headerView.findViewById(R.id.subtitle);
        }
    }

    class NavigationCustomActionViews {
        ViewGroup vgRoot;
        ImageView ivAction;
        ProgressBar pbAction;

        private void bindTo(View actionView) {
            vgRoot = actionView.findViewById(R.id.vg_root);
            ivAction = actionView.findViewById(R.id.iv_action);
            pbAction = actionView.findViewById(R.id.pb_action);
        }
    }
}
