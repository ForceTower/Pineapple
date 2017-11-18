package com.forcetower.uefs.sdk;

import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

/**
 * Created by João Paulo on 17/11/2017.
 */

public class SagresPortalSDK {
    private static final Object LOCK = new Object();
    private static Executor executor;
    private static boolean sdkInitialized = false;
    private static Context applicationContext = null;

    private static void initializeSdk(final Context context) {
        if (sdkInitialized) return;

        applicationContext = context;
        sdkInitialized = true;

        FutureTask<Void> futureTask = new FutureTask<>(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                SagresAccessManager.getInstance().loadCurrentAccess();
                SagresProfileManager.getInstance().loadCurrentProfile();

                if (SagresAccess.getCurrentAccess() != null && SagresProfile.getCurrentProfile() == null) {
                    SagresProfile.fetchProfileForCurrentAccess();
                }

                SagresMethods.instantiate();
                return null;
            }
        });

        getExecutor().execute(futureTask);
    }

    public static Context getApplicationContext() {
        return applicationContext;
    }

    public static boolean isSdkInitialized() {
        return sdkInitialized;
    }

    public static Executor getExecutor() {
        synchronized (LOCK) {
            if (executor == null) {
                executor = AsyncTask.THREAD_POOL_EXECUTOR;
            }
        }
        return executor;
    }
}
