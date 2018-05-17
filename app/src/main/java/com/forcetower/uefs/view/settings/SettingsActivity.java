package com.forcetower.uefs.view.settings;

import android.Manifest;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.forcetower.uefs.R;
import com.forcetower.uefs.anim.ProgressBarAnimation;
import com.forcetower.uefs.rep.sgrs.LoginRepository;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.GeneralUtils;
import com.forcetower.uefs.util.NetworkUtils;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.vm.google.GoogleCalendarViewModel;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import dagger.android.AndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class SettingsActivity extends UBaseActivity implements SettingsController, HasSupportFragmentInjector, EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_ACCOUNT_PICKER_RESET = 1001;
    private static final int REQUEST_AUTHORIZATION = 1002;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1003;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1004;
    private static final String SHARED_PREF_ACCOUNT_NAME = "com.forcetower.uefs.calendar.ACCOUNT_SELECTED";

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.pb_progress)
    ProgressBar pbProgress;

    @Inject
    LoginRepository repository;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    FirebaseJobDispatcher dispatcher;

    private GoogleAccountCredential googleCredential;
    private GoogleCalendarViewModel calendarViewModel;

    private int currentProgress;
    private int currentSelect;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_settings, savedInstanceState);
        setupToolbar();

        getFragmentManager().beginTransaction().replace(R.id.container, new SettingsFragment()).commit();

        googleCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), CalendarScopes.all()).setBackOff(new ExponentialBackOff());
        calendarViewModel = ViewModelProviders.of(this, viewModelFactory).get(GoogleCalendarViewModel.class);
        calendarViewModel.getExportData().observe(this, this::onExportProgress);
        calendarViewModel.getResetData().observe(this, this::onResetProgress);

        if (savedInstanceState != null) {
            currentProgress = savedInstanceState.getInt("PROGRESS");
            pbProgress.setProgress(currentProgress);
            currentSelect = savedInstanceState.getInt("CURRENT_SELECT");
        } else {
            currentSelect = REQUEST_ACCOUNT_PICKER;
        }
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    private void setupToolbar() {
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (VersionUtils.isLollipop()) toolbar.setElevation(10);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.settings);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    @Override
    public void exportToCalendar() {
        Timber.d("Export to Calendar");
        if (!calendarViewModel.isExporting()) {
            Timber.d("Triggers Export");
            pbProgress.setIndeterminate(true);
            pbProgress.setProgress(0);
            AnimUtils.fadeIn(this, pbProgress);
            currentSelect = REQUEST_ACCOUNT_PICKER;
            exportToGoogleCalendar();
        } else {
            Toast.makeText(this, R.string.wait_until_operation_completes, Toast.LENGTH_SHORT).show();
            Timber.d("An operation is being executed");
        }
    }

    @Override
    public void resetExportToCalendar() {
        Timber.d("Reset export to calendar");
        if (!calendarViewModel.isExporting()) {
            Timber.d("Triggers Reset");
            AnimUtils.fadeIn(this, pbProgress);
            pbProgress.setIndeterminate(false);
            pbProgress.setProgress(0);
            currentSelect = REQUEST_ACCOUNT_PICKER_RESET;
            resetExportToGoogleCalendar();
        } else {
            Toast.makeText(this, R.string.wait_until_operation_completes, Toast.LENGTH_SHORT).show();
            Timber.d("An operation is being executed");
        }
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return null;
    }

    @SuppressWarnings("ConstantConditions")
    private void onExportProgress(Resource<Integer> resource) {
        if (resource == null) {
            Timber.d("Null resource");
        } else {
            if (resource.status == Status.ERROR) {
                if (!handleErrorCorrectly(resource)) {
                    Timber.d(getString(resource.data));
                    if (resource.data == R.string.failed_to_export_class)
                        Toast.makeText(this, getString(R.string.failed_to_export_class) + " " + resource.message, Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(this, getString(resource.data), Toast.LENGTH_SHORT).show();
                        AnimUtils.fadeOut(this, pbProgress);
                    }
                }
            } else if (resource.status == Status.LOADING) {
                Timber.d("Loading");
                pbProgress.setIndeterminate(true);
            } else {
                pbProgress.setIndeterminate(false);
                if (resource.data == 1000) {
                    AnimUtils.fadeOut(this, pbProgress);
                    currentProgress = 0;
                    Toast.makeText(this, R.string.completed, Toast.LENGTH_SHORT).show();
                } else {
                    ProgressBarAnimation anim = new ProgressBarAnimation(pbProgress, pbProgress.getProgress(), resource.data);
                    anim.setDuration(500);
                    currentProgress = resource.data;
                    pbProgress.startAnimation(anim);
                }
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void onResetProgress(Resource<Integer> resource) {
        if (resource.status == Status.LOADING) {
            pbProgress.setIndeterminate(false);
            if (resource.data == 1000) {
                AnimUtils.fadeOut(this, pbProgress);
                currentProgress = 0;
            } else {
                ProgressBarAnimation anim = new ProgressBarAnimation(pbProgress, pbProgress.getProgress(), resource.data);
                anim.setDuration(500);
                currentProgress = resource.data;
                pbProgress.startAnimation(anim);
            }
        } else if (resource.status == Status.ERROR) {
            handleErrorCorrectly(resource);
        } else {
            Toast.makeText(this, R.string.completed, Toast.LENGTH_SHORT).show();
            currentProgress = 0;
            AnimUtils.fadeOut(this, pbProgress);
        }
    }

    private boolean handleErrorCorrectly(Resource<Integer> resource) {
        Throwable throwable = resource.throwable;
        if (resource.code == 350 && throwable != null) {
            showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) throwable).getConnectionStatusCode());
            currentProgress = 0;
            AnimUtils.fadeOut(this, pbProgress);
            return true;
        } else if (resource.code == 360 && throwable != null) {
            startActivityForResult(((UserRecoverableAuthIOException) throwable).getIntent(), REQUEST_AUTHORIZATION);
            currentProgress = 0;
            AnimUtils.fadeOut(this, pbProgress);
            return true;
        }
        return false;
    }

    private void exportToGoogleCalendar() {
        if (!GeneralUtils.isGooglePlayServicesAvailable(this)) {
            Timber.d("Google Play services unavailable");
            acquireGooglePlayServices();
        } else if (googleCredential.getSelectedAccountName() == null) {
            Timber.d("Account is not selected yet");
            chooseGoogleAccount(REQUEST_ACCOUNT_PICKER);
        } else if (!NetworkUtils.isNetworkAvailable(this)) {
            Timber.d("No internet");
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        } else {
            Timber.d("All steps completed. It will do things now");
            calendarViewModel.exportData(googleCredential);
        }
    }

    private void resetExportToGoogleCalendar() {
        if (!GeneralUtils.isGooglePlayServicesAvailable(this)) {
            Timber.d("Google Play services unavailable");
            acquireGooglePlayServices();
        } else if (googleCredential.getSelectedAccountName() == null) {
            Timber.d("Account is not selected yet");
            chooseGoogleAccount(REQUEST_ACCOUNT_PICKER_RESET);
        } else if (!NetworkUtils.isNetworkAvailable(this)) {
            Timber.d("No internet");
            Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        } else {
            Timber.d("All steps completed. It will do things now");
            calendarViewModel.resetExportedSchedule(googleCredential, false);
        }
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseGoogleAccount() {
        chooseGoogleAccount(currentSelect);
    }

    private void chooseGoogleAccount(int request) {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.GET_ACCOUNTS)) {
            Timber.d("Has permissions");
            String account = getPreferences(Context.MODE_PRIVATE).getString(SHARED_PREF_ACCOUNT_NAME, null);
            if (account == null) {
                startActivityForResult(googleCredential.newChooseAccountIntent(), request);
            } else {
                googleCredential.setSelectedAccountName(account);
                if (request == REQUEST_ACCOUNT_PICKER)
                    exportToGoogleCalendar();
                else
                    resetExportToGoogleCalendar();
            }
        } else {
            Timber.d("Doesn't have permissions");
            EasyPermissions.requestPermissions(this,
                    getString(R.string.permission_needed_get_accounts),
                    REQUEST_PERMISSION_GET_ACCOUNTS, Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GOOGLE_PLAY_SERVICES) {
            if (resultCode != RESULT_OK) {
                Timber.d("Result code for play services is not ok");
                Toast.makeText(this, R.string.feature_requires_google_play_services, Toast.LENGTH_SHORT).show();
            } else {
                Timber.d("Result code for play services is OK");
                exportToGoogleCalendar();
            }
        } else if (requestCode == REQUEST_ACCOUNT_PICKER || requestCode == REQUEST_ACCOUNT_PICKER_RESET) {
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                Timber.d("Received account name %s", accountName);
                if (accountName != null) {
                    SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(SHARED_PREF_ACCOUNT_NAME, accountName);
                    editor.apply();
                    googleCredential.setSelectedAccountName(accountName);
                    if (requestCode == REQUEST_ACCOUNT_PICKER)
                        exportToGoogleCalendar();
                    else
                        resetExportToGoogleCalendar();
                }
            }
        } else if (requestCode == REQUEST_AUTHORIZATION) {
            if (resultCode == RESULT_OK) {
                if (currentSelect == REQUEST_ACCOUNT_PICKER)
                    exportToGoogleCalendar();
                else
                    resetExportToGoogleCalendar();
            }
        }
    }

    @Override
    public FirebaseJobDispatcher getDispatcher() {
        return dispatcher;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) { }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) { }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("PROGRESS", currentProgress);
        outState.putInt("CURRENT_SELECT", currentSelect);
        super.onSaveInstanceState(outState);
    }
}
