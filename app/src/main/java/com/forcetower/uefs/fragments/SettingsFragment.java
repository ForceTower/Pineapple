package com.forcetower.uefs.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.forcetower.uefs.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference notification = findPreference("show_message_notification");
        Preference syncFrequency = findPreference("sync_frequency");

        if (key.equalsIgnoreCase("sync_frequency")) {
            String newValue = sharedPreferences.getString(key, "");
            if (!newValue.trim().isEmpty()) {
                int frequency = Integer.parseInt(newValue);

                if (frequency == -1) {
                    syncFrequency.setSummary(R.string.pref_sync_frequency_never);
                    notification.setEnabled(false);
                } else {
                    syncFrequency.setSummary(R.string.pref_sync_frequency_enabled);
                    notification.setEnabled(true);
                }
            }
        }

    }
}
