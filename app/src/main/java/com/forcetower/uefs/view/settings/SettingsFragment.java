package com.forcetower.uefs.view.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.work.sync.SyncWorkerUtils;

import timber.log.Timber;

/**
 * Created by João Paulo on 09/03/2018.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SettingsController controller;
    private SharedPreferences preferences;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            controller = (SettingsController) getActivity();
            Timber.d("Attached");
        } catch (ClassCastException ignored) {
            Timber.d("Activity %s must implement SettingsController", getActivity().getClass().getSimpleName());
        }
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        preferences = getPreferenceScreen().getSharedPreferences();
        preferences.registerOnSharedPreferenceChangeListener(this);

        findPreference("export_to_google_calendar").setOnPreferenceClickListener(preference -> exportToCalendar());
        findPreference("reset_calendar_export").setOnPreferenceClickListener(preference -> resetExportToCalendar());

        oreoConfiguration();
    }

    private boolean resetExportToCalendar() {
        controller.resetExportToCalendar();
        return true;
    }

    private boolean exportToCalendar() {
        controller.exportToCalendar();
        return true;
    }

    @TargetApi(26)
    private void oreoConfiguration() {
        Preference messages = findPreference("notification_message_posted");
        if (messages != null) messages.setOnPreferenceClickListener(preference -> notificationControl(Constants.CHANNEL_MESSAGES_ID));

        Preference posted = findPreference("notification_grades_posted");
        if (posted != null) posted.setOnPreferenceClickListener(preference -> notificationControl(Constants.CHANNEL_GRADES_POSTED_ID));

        Preference created = findPreference("notification_grades_created");
        if (created != null) created.setOnPreferenceClickListener(preference -> notificationControl(Constants.CHANNEL_GRADES_CREATED_ID));

        Preference updated = findPreference("notification_grades_date_change");
        if (updated != null) updated.setOnPreferenceClickListener(preference -> notificationControl(Constants.CHANNEL_GRADES_CHANGED_ID));

        Preference eventGen = findPreference("notification_events_general");
        if (eventGen != null) eventGen.setOnPreferenceClickListener(preference -> notificationControl(Constants.CHANNEL_EVENTS_GENERAL_ID));

        Preference dceMsg = findPreference("notification_dce_messages");
        if (dceMsg != null) dceMsg.setOnPreferenceClickListener(preference -> notificationControl(Constants.CHANNEL_MESSAGES_DCE_ID));
    }

    @TargetApi(26)
    private boolean notificationControl(String channelId) {
        Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, controller.getContext().getPackageName());
        intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
        controller.getContext().startActivity(intent);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equalsIgnoreCase("sync_frequency")) {
            updateSyncFrequency(sharedPreferences, key);
        } else if (key.equalsIgnoreCase("new_schedule_layout")) {
            Toast.makeText(controller.getContext(), R.string.changes_apply_after_restart, Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSyncFrequency(SharedPreferences preferences, String key) {
        Preference preference = findPreference("sync_frequency");
        Preference notification = findPreference("show_message_notification");
        Preference gradePosted  = findPreference("show_grades_posted_notification");
        Preference gradeCreated = findPreference("show_grades_created_notification");
        Preference gradeChanged = findPreference("show_grades_changed_notification");
        String newValue = preferences.getString(key, "60");
        int frequency = 60;
        try {
            frequency = Integer.parseInt(newValue);
        } catch (Exception ignored) {}

        if (frequency == -1) {
            preference.setSummary(R.string.pref_sync_frequency_never);
            if (notification != null) notification.setEnabled(false);
            if (gradePosted  != null) gradePosted.setEnabled (false);
            if (gradeCreated != null) gradeCreated.setEnabled(false);
            if (gradeChanged != null) gradeChanged.setEnabled(false);
            Timber.d("Frequency set to never update");
            SyncWorkerUtils.disableWorker(controller.getContext());
        } else if (frequency > 0) {
            preference.setSummary(R.string.pref_sync_frequency_enabled);
            if (notification != null) notification.setEnabled(true);
            if (gradePosted  != null) gradePosted.setEnabled (true);
            if (gradeCreated != null) gradeCreated.setEnabled(true);
            if (gradeChanged != null) gradeChanged.setEnabled(true);
            Timber.d("Frequency set to %d minutes", frequency);
            SyncWorkerUtils.createSync( controller.getContext(), frequency, false);
        }
    }
}
