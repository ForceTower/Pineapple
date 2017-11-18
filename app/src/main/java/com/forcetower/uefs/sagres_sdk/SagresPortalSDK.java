package com.forcetower.uefs.sagres_sdk;

import android.content.Context;
import android.os.AsyncTask;

import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.managers.SagresAccessManager;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.managers.SagresProfileManager;
import com.forcetower.uefs.sagres_sdk.utility.SagresCookieJar;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;

import okhttp3.OkHttpClient;

/**
 * Created by João Paulo on 17/11/2017.
 */

public class SagresPortalSDK {
    public static final String SAGRES_SDK_TAG = "Sagres SDK";
    private static final Object LOCK = new Object();
    private static Executor executor;
    private static boolean sdkInitialized = false;
    private static Context applicationContext = null;

    private static OkHttpClient httpClient;

    private static void initializeSdk(final Context context) {
        if (sdkInitialized) return;

        CookieHandler cookieHandler = new CookieManager();

        httpClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .cookieJar(new SagresCookieJar(cookieHandler))
                .build();

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

    public static OkHttpClient getHttpClient() {
        return httpClient;
    }
}
