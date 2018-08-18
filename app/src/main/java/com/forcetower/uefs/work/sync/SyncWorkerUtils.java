package com.forcetower.uefs.work.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.forcetower.uefs.Constants;
import com.forcetower.uefs.alm.RefreshAlarmTrigger;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

/**
 * Created by João Paulo on 21/06/2018.
 */
public class SyncWorkerUtils {

    public static void createSync(FirebaseJobDispatcher dispatcher, Context context, int frequency, boolean force) {
        if (frequency == -1) {
            RefreshAlarmTrigger.disableBootComponent(context);
            disableWorker(context, dispatcher);
        } else {
            RefreshAlarmTrigger.enableBootComponent(context);
            createWorker(dispatcher, context, frequency, force);
        }
    }

    private static void createWorker(FirebaseJobDispatcher dispatcher, Context context, int frequency, boolean force) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int current = preferences.getInt("curr_sync_frequency", 40);
        boolean equals = current == frequency && !force;

        if (!equals) RefreshAlarmTrigger.create(context, frequency);

        JobSyncUtils.setupSagresSync(dispatcher, context, frequency);


        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                SagresSyncWorker.class, frequency, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag(Constants.WORKER_SYNC_SAGRES_NAME)
                .build();

        WorkManager.getInstance()
                .enqueueUniquePeriodicWork(
                        Constants.WORKER_SYNC_SAGRES_UNIQUE,
                        equals ? ExistingPeriodicWorkPolicy.KEEP : ExistingPeriodicWorkPolicy.REPLACE,
                        workRequest
                );

        preferences.edit().putInt("curr_sync_frequency", frequency).apply();
    }

    public static void disableWorker(Context context, FirebaseJobDispatcher dispatcher) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putInt("curr_sync_frequency", -1).apply();
        WorkManager.getInstance().cancelAllWorkByTag(Constants.WORKER_SYNC_SAGRES_NAME);
        RefreshAlarmTrigger.disableBootComponent(context);
        JobSyncUtils.cancelSyncService(dispatcher, context);
    }
}
