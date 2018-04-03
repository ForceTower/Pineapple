package com.forcetower.uefs.view.connected.fragments;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.di.Injectable;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.util.AnimUtils;
import com.forcetower.uefs.util.GeneralUtils;
import com.forcetower.uefs.util.NetworkUtils;
import com.forcetower.uefs.view.connected.LocationClickListener;
import com.forcetower.uefs.view.connected.NavigationController;
import com.forcetower.uefs.view.connected.adapters.NewScheduleAdapter;
import com.forcetower.uefs.view.connected.adapters.ScheduleAdapter;
import com.forcetower.uefs.view.discipline.DisciplineDetailsActivity;
import com.forcetower.uefs.vm.GoogleCalendarViewModel;
import com.forcetower.uefs.vm.ScheduleViewModel;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.CalendarScopes;
import com.google.firebase.auth.GoogleAuthCredential;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * Created by João Paulo on 29/03/2018.
 */
public class NewScheduleFragment extends Fragment implements Injectable, EasyPermissions.PermissionCallbacks {
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_AUTHORIZATION = 1001;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    private static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;
    private static final String SHARED_PREF_ACCOUNT_NAME = "com.forcetower.uefs.calendar.ACCOUNT_SELECTED";

    @BindView(R.id.vg_no_schedule)
    ViewGroup vgNoSchedule;
    @BindView(R.id.recycler_view)
    RecyclerView rvSchedule;
    @BindView(R.id.rv_schedule_subtitle)
    RecyclerView rvScheduleSubtitle;
    @BindView(R.id.sv_schedule)
    NestedScrollView svSchedule;
    @BindView(R.id.btn_export_google_calendar)
    Button btnExportToGoogle;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    @Inject
    AppExecutors executors;

    private NavigationController controller;
    private ScheduleViewModel scheduleViewModel;

    private NewScheduleAdapter scheduleAdapter;
    private ScheduleAdapter subtitleAdapter;

    private GoogleAccountCredential googleCredential;
    private GoogleCalendarViewModel calendarViewModel;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            controller = (NavigationController) context;
        } catch (ClassCastException e) {
            Timber.e("Class %s must implement NavigationController", context.getClass().getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_new, container, false);
        ButterKnife.bind(this, view);
        btnExportToGoogle.setOnClickListener(v -> exportToGoogleCalendar());
        setupRecycler();
        setupSubtitles();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        scheduleViewModel = ViewModelProviders.of(this, viewModelFactory).get(ScheduleViewModel.class);
        scheduleViewModel.getSchedule(null).observe(this, this::onReceiveLocations);
        googleCredential = GoogleAccountCredential.usingOAuth2(requireActivity().getApplicationContext(), CalendarScopes.all()).setBackOff(new ExponentialBackOff());
        calendarViewModel = ViewModelProviders.of(this, viewModelFactory).get(GoogleCalendarViewModel.class);
        calendarViewModel.getExportData().observe(this, this::onExportProgress);
    }

    private void onExportProgress(Resource<Integer> resource) {
        if (resource == null) {
            Timber.d("Null resource");
            return;
        } else {
            //noinspection ConstantConditions
            Timber.d(getString(resource.data));
        }
    }

    private void setupRecycler() {
        scheduleAdapter = new NewScheduleAdapter(requireContext(), new ArrayList<>());
        scheduleAdapter.setOnClickListener(locationClickListener);
        rvSchedule.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSchedule.setAdapter(scheduleAdapter);
        rvSchedule.setNestedScrollingEnabled(false);
    }

    private void setupSubtitles() {
        subtitleAdapter = new ScheduleAdapter(getContext(), new ArrayList<>(), true);
        subtitleAdapter.setOnClickListener(locationClickListener);
        rvScheduleSubtitle.setLayoutManager(new LinearLayoutManager(getContext()));
        rvScheduleSubtitle.setAdapter(subtitleAdapter);
        rvScheduleSubtitle.setNestedScrollingEnabled(false);
    }

    private void onReceiveLocations(List<DisciplineClassLocation> locations) {
        if (locations == null || locations.isEmpty()) {
            AnimUtils.fadeOut(getContext(), svSchedule);
            AnimUtils.fadeIn(getContext(), vgNoSchedule);
        } else {
            AnimUtils.fadeOut(getContext(), vgNoSchedule);
            vgNoSchedule.setVisibility(View.GONE);
            svSchedule.setVisibility(View.VISIBLE);
            try {
                scheduleAdapter.setLocations(locations);
                subtitleAdapter.setLocations(locations);
                Timber.d(locations.get(0).getDay());
            } catch (Exception ex) {
                controller.showNewScheduleError(ex);
            }
        }
    }

    private LocationClickListener locationClickListener = location -> executors.others().execute(() -> {
        int groupId = location.getGroupId();
        DisciplineGroup group = scheduleViewModel.getDisciplineGroupDirect(groupId);
        int disciplineId = group.getDiscipline();
        if (getContext() != null) {
            DisciplineDetailsActivity.startActivity(getContext(), groupId, disciplineId);
        }
    });

    private void exportToGoogleCalendar() {
        if (!GeneralUtils.isGooglePlayServicesAvailable(requireActivity())) {
            Timber.d("Google Play services unavailable");
            acquireGooglePlayServices();
        } else if (googleCredential.getSelectedAccountName() == null) {
            Timber.d("Account is not selected yet");
            chooseGoogleAccount();
        } else if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            Timber.d("No internet");
            Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
        } else {
            Timber.d("All steps completed. It will do things now");
            calendarViewModel.exportData(googleCredential);
        }
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(requireActivity());
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(requireActivity(), connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseGoogleAccount() {
        if (EasyPermissions.hasPermissions(requireContext(), Manifest.permission.GET_ACCOUNTS)) {
            Timber.d("Has permissions");
            String account = requireActivity().getPreferences(Context.MODE_PRIVATE).getString(SHARED_PREF_ACCOUNT_NAME, null);
            if (account == null) {
                startActivityForResult(googleCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);
            } else {
                googleCredential.setSelectedAccountName(account);
                exportToGoogleCalendar();
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
                Toast.makeText(requireContext(), R.string.feature_requires_google_play_services, Toast.LENGTH_SHORT).show();
            } else {
                Timber.d("Result code for play services is OK");
                exportToGoogleCalendar();
            }
        } else if (requestCode == REQUEST_ACCOUNT_PICKER) {
            if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                Timber.d("Received account name %s", accountName);
                if (accountName != null) {
                    SharedPreferences settings = requireActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(SHARED_PREF_ACCOUNT_NAME, accountName);
                    editor.apply();
                    googleCredential.setSelectedAccountName(accountName);
                    exportToGoogleCalendar();
                }
            }
        } else if (requestCode == REQUEST_AUTHORIZATION) {
            if (resultCode == RESULT_OK) {
                exportToGoogleCalendar();
            }
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) { }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) { }
}
