package com.forcetower.uefs.work.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

/**
 * Created by João Paulo on 21/06/2018.
 */
public class SyncWorkerUtils {

    public static void createSync(Context context, int frequency, boolean force) {
        if (frequency == -1) {
            disableWorker(context);
        } else {
            createWorker(context, frequency, force);
        }
    }

    private static void createWorker(Context context, int frequency, boolean force) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int current = preferences.getInt("curr_sync_frequency_job", 40);
        boolean update = current != frequency || force;
        Timber.d("Should Update Current? " + update + ", was forced? " + force);
        if (update) {
            new JobRequest.Builder(SagresSyncWorker.TAG)
                    .setPeriodic(TimeUnit.MINUTES.toMillis(frequency), TimeUnit.MINUTES.toMillis(5))
                    .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                    .setUpdateCurrent(true)
                    .setRequirementsEnforced(true)
                    .build()
                    .schedule();
        }

        preferences.edit().putInt("curr_sync_frequency_job", frequency).apply();
    }

    public static void disableWorker(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putInt("curr_sync_frequency_job", -1).apply();
        int count = JobManager.instance().cancelAllForTag(SagresSyncWorker.TAG);
        Timber.d("Job count cancelled: " + count);
    }
}
