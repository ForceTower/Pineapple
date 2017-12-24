package com.forcetower.uefs.sagres_sdk;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.managers.SagresAccessManager;
import com.forcetower.uefs.sagres_sdk.managers.SagresProfileManager;
import com.forcetower.uefs.sagres_sdk.utility.SagresCookieJar;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

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
    private static List<LoginListener> loginListeners;
    private static CookieHandler cookieHandler;

    public static void initializeSdk(final Context context) {
        initializeSdk(context, null);
    }

    public static void initializeSdk(final Context context, final SagresSDKInitializationCallback callback) {
        if (sdkInitialized) {
            if (callback != null)
                callback.onFinishInit();
            return;
        }

        Log.i(SAGRES_SDK_TAG, "Initializing Sagres SDK - " + Calendar.getInstance().getTime().toString());

        cookieHandler = new CookieManager();
        httpClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .cookieJar(new SagresCookieJar(cookieHandler))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        loginListeners = new ArrayList<>();
        sdkInitialized = true;
        applicationContext = context;

        //TODO This will be moved to a new section where all this stuff can happen syncronous
        FutureTask<Void> futureTask = new FutureTask<>(() -> {
            SagresAccessManager.getInstance().loadCurrentAccess();
            SagresProfileManager.getInstance().loadCurrentProfile();

            if (SagresAccess.getCurrentAccess() != null && SagresProfile.getCurrentProfile() == null) {
                Log.i(SAGRES_SDK_TAG, "Attempt to load user profile");
                SagresProfile.fetchProfileForCurrentAccess();
            }

            callback.onFinishInit();
            return null;
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

    public static void logout() {
        SagresAccess.setCurrentAccess(null);
        SagresProfile.setCurrentProfile(null);

        cookieHandler = new CookieManager();
        httpClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .cookieJar(new SagresCookieJar(cookieHandler))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static OkHttpClient getHttpClient() {
        return httpClient;
    }

    public static void alertConnectionListeners(int code, String string) {
        for (LoginListener listener : loginListeners) {
            listener.alert(code, string);
        }
    }

    public static void addLoginListener(LoginListener loginListener) {
        loginListeners.add(loginListener);
    }

    public static void removeLoginListener(LoginListener loginListener) {
        loginListeners.remove(loginListener);
    }

    public static void resetHttpClient() {
        cookieHandler = new CookieManager();
        httpClient = new OkHttpClient.Builder()
                .followRedirects(true)
                .cookieJar(new SagresCookieJar(cookieHandler))
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    public static void setContext(Context context) {
        applicationContext = context;
    }

    public interface SagresSDKInitializationCallback {
        void onFinishInit();
    }

    public interface LoginListener {
        void alert(int code, String string);
    }
}
