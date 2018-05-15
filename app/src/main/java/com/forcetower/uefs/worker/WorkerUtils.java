package com.forcetower.uefs.worker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.forcetower.uefs.Constants;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import timber.log.Timber;

/**
 * Created by João Paulo on 10/05/2018.
 */
public class WorkerUtils {

    public static void setupSagresSync(Context context, int frequency) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int diffFreq = preferences.getInt("sagres_sync_worker_frequency", -1);
        boolean conf = preferences.getBoolean("sagres_sync_worker_configured_R", false);
        Timber.d("%s condition", frequency == -1 || (conf && diffFreq == frequency));
        Timber.d("Frequency: %d", frequency);
        Timber.d("Configured: %s", conf);
        Timber.d("Diff Freq: %d", diffFreq);
        if (frequency == -1 || (conf && diffFreq == frequency)) {
            Timber.d("Worker Configuration Completed");
            return;
        }
        Timber.d("Creating configuration");

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        WorkManager.getInstance().cancelAllWorkByTag(Constants.WORKER_SYNC_SAGRES_NAME);

        PeriodicWorkRequest sagresSyncWorker
                = new PeriodicWorkRequest.Builder(SagresSyncWorker.class, frequency, TimeUnit.MINUTES)
                .addTag(Constants.WORKER_SYNC_SAGRES_NAME)
                .setConstraints(constraints)
                .build();

        WorkManager.getInstance().enqueue(sagresSyncWorker);
        preferences.edit()
                .putBoolean("sagres_sync_worker_configured_R", true)
                .putInt("sagres_sync_worker_frequency", frequency)
                .apply();
    }

    public static void disableSagresSync(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Timber.d("Configuration erased");
        WorkManager.getInstance().cancelAllWorkByTag(Constants.WORKER_SYNC_SAGRES_NAME);
        preferences.edit().putBoolean("sagres_sync_worker_configured_R", false).apply();
        preferences.edit().putInt("sagres_sync_worker_frequency", -1).apply();
    }
}
