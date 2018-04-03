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

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.alm.RefreshAlarmTrigger;
import com.forcetower.uefs.view.about.AboutActivity;
import com.forcetower.uefs.view.suggestion.SuggestionActivity;

import timber.log.Timber;

/**
 * Created by João Paulo on 09/03/2018.
 */

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SettingsController controller;

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
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        findPreference("logoff_key").setOnPreferenceClickListener(preference -> logout());
        findPreference("feedback_key").setOnPreferenceClickListener(preference -> feedback());
        findPreference("about_app_key").setOnPreferenceClickListener(preference -> about());

        oreoConfiguration();
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
    }

    private boolean about() {
        AboutActivity.startActivity(getActivity());
        return true;
    }

    private boolean feedback() {
        SuggestionActivity.startActivity(getActivity());
        return true;
    }

    private boolean logout() {
        controller.logout();
        return true;
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
        if (key.equalsIgnoreCase("sync_frequency"))
            updateSyncFrequency(sharedPreferences, key);
    }

    private void updateSyncFrequency(SharedPreferences preferences, String key) {
        Preference preference = findPreference("sync_frequency");
        Preference notification = findPreference("show_message_notification");
        String newValue = preferences.getString(key, "");
        int frequency = Integer.parseInt(newValue);

        if (frequency == -1) {
            preference.setSummary(R.string.pref_sync_frequency_never);
            if (notification != null) notification.setEnabled(false);
            RefreshAlarmTrigger.removeAlarm(getActivity());
            RefreshAlarmTrigger.disableBootComponent(getActivity());
            Timber.d("Frequency set to never update");
        } else if (frequency > 0) {
            preference.setSummary(R.string.pref_sync_frequency_enabled);
            if (notification != null) notification.setEnabled(true);
            RefreshAlarmTrigger.create(getActivity(), frequency);
            RefreshAlarmTrigger.enableBootComponent(controller.getContext());
            Timber.d("Frequency set to %d minutes", frequency);
        }
    }
}
