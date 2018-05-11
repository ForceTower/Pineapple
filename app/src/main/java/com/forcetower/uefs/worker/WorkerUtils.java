package com.forcetower.uefs.worker;

import com.forcetower.uefs.Constants;

import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

/**
 * Created by João Paulo on 10/05/2018.
 */
public class WorkerUtils {

    public static void setupSagresSync(int frequency) {
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
    }

    public static void disableSagresSync() {
        WorkManager.getInstance().cancelAllWorkByTag(Constants.WORKER_SYNC_SAGRES_NAME);
    }
}
