package com.forcetower.uefs.alm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import timber.log.Timber;

public class BootOptionsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            Timber.d("Why is android trolling???");
            return;
        }

        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            String strFrequency = PreferenceManager.getDefaultSharedPreferences(context).getString("sync_frequency", "60");
            int frequency = 60;
            try {
                frequency = Integer.parseInt(strFrequency);
            } catch (Exception ignored) {}

            Timber.d("Boot received! Starting Alarm triggers");
            if (frequency != -1) RefreshAlarmTrigger.create(context, frequency);
            else                 RefreshAlarmTrigger.removeAlarm(context);
        }
    }
}