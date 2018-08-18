package com.forcetower.uefs.rcv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.work.sync.SyncWorkerUtils;

import javax.inject.Inject;

import androidx.work.impl.WorkManagerImpl;
import dagger.android.AndroidInjection;

public class UpdateReceiver extends BroadcastReceiver {
    @Inject
    AppExecutors executors;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction()))
            return;

        AndroidInjection.inject(this, context);

        Log.i("UpdateReceiver", "App just got updated. Preparing to Clear Database");


        WorkManagerImpl instance = WorkManagerImpl.getInstance();
        if (instance != null) {
            executors.diskIO().execute(() -> {
                instance.getWorkDatabase().clearAllTables();
                String strFrequency = PreferenceManager.getDefaultSharedPreferences(context)
                        .getString("sync_frequency", "60");
                int frequency = 60;
                try {
                    frequency = Integer.parseInt(strFrequency);
                } catch (Exception ignored) {}
                SyncWorkerUtils.createSync(context, frequency, true);
                Log.i("UpdateReceiver", "Database Cleared and tasks updated");
            });
        } else {
            Log.i("UpdateReceiver", "WorkManager instance is null");
        }

    }
}
