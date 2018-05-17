package com.forcetower.uefs.worker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.forcetower.uefs.Constants;

import timber.log.Timber;

/**
 * Created by João Paulo on 17/05/2018.
 */
public class SyncUtils {

    public static void setupSagresSync(FirebaseJobDispatcher dispatcher, Context context, int frequency, boolean application) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean configured = preferences.getBoolean("sagres_sync_worker_configured_application", false);
        if (application && configured) return;

        setupSagresSync(dispatcher, context, frequency);
    }

    public static void setupSagresSync(FirebaseJobDispatcher dispatcher, Context context, int frequency) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        int oldFreq = preferences.getInt("sagres_sync_worker_frequency", 60);
        boolean replace = oldFreq == frequency;
        Timber.d("Should replace current sync? %s", replace);

        if (frequency == oldFreq) return;
        if (frequency == -1) {
            cancelSyncService(dispatcher, context);
            return;
        }

        Job sync = dispatcher.newJobBuilder()
                .setService(SagresSyncJobService.class)
                .addConstraint(Constraint.ON_ANY_NETWORK)
                .setTag(Constants.WORKER_SYNC_SAGRES_NAME)
                .setRecurring(true)
                .setReplaceCurrent(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow((frequency - 10)*60, (frequency + 5)*60))
                .build();

        try {
            dispatcher.mustSchedule(sync);
            preferences.edit()
                    .putBoolean("sagres_sync_worker_configured_application", true)
                    .putInt("sagres_sync_worker_frequency", frequency)
                    .apply();
        } catch (Exception e) {
            Timber.e("Scheduling failed");
            e.printStackTrace();
        }
    }

    public static void cancelSyncService(FirebaseJobDispatcher dispatcher, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Timber.d("Configuration erased");
        dispatcher.cancel(Constants.WORKER_SYNC_SAGRES_NAME);
        preferences.edit().putBoolean("sagres_sync_worker_configured_application", false).apply();
        preferences.edit().putInt("sagres_sync_worker_frequency", -1).apply();
    }
}
