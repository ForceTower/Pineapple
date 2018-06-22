package com.forcetower.uefs.work.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.forcetower.uefs.Constants;

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

    public static void createSync(Context context, int frequency) {
        if (frequency == -1) {
            disableWorker(context);
        } else {
            createWorker(context, frequency);
        }
    }

    private static void createWorker(Context context, int frequency) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int current = preferences.getInt("current_sync_frequency", 60);
        boolean equals = current == frequency;

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

        preferences.edit().putInt("current_sync_frequency", frequency).apply();
    }

    public static void disableWorker(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putInt("current_sync_frequency", -1).apply();
        WorkManager.getInstance().cancelAllWorkByTag(Constants.WORKER_SYNC_SAGRES_NAME);
    }
}
