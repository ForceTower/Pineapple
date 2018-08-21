package com.forcetower.uefs;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.preference.PreferenceManager;
import androidx.multidex.MultiDex;
import androidx.appcompat.app.AppCompatDelegate;

import com.forcetower.uefs.di.AppInjector;
import com.forcetower.uefs.di.component.AppComponent;
import com.forcetower.uefs.di.injector.HasLollipopServiceInjector;
import com.forcetower.uefs.ntf.NotificationHelper;
import com.forcetower.uefs.sync.SyncConfiguration;
import com.forcetower.uefs.work.sync.SyncWorkerUtils;
import com.squareup.picasso.Picasso;

import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasBroadcastReceiverInjector;
import dagger.android.HasServiceInjector;
import timber.log.Timber;

/**
 * Created by João Paulo on 05/03/2018.
 * Application Class. The android kick start
 */
public class UEFSApplication extends Application implements HasActivityInjector,
        HasServiceInjector, HasBroadcastReceiverInjector, HasLollipopServiceInjector {
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingActivityAndroidInjector;
    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceAndroidInjector;
    @Inject
    DispatchingAndroidInjector<BroadcastReceiver> dispatchingBroadcastAndroidInjector;

    private AppComponent appComponent;
    private Map<String, Document> documents;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        documents = new HashMap<>();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        appComponent = AppInjector.init(this);

        configureFeatures();

        Picasso.Builder builder = new Picasso.Builder(this);
        Picasso.setSingletonInstance(builder.build());
    }

    private void configureFeatures() {
        new NotificationHelper(this).createChannels();
        String strFrequency = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("sync_frequency", "60");
        int frequency = 60;
        try {
            frequency = Integer.parseInt(strFrequency);
        } catch (Exception ignored) {}
        SyncWorkerUtils.createSync(this, frequency);
        SyncConfiguration.initializeSyncAdapter(this);
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingActivityAndroidInjector;
    }

    @Override
    public AndroidInjector<Service> serviceInjector() {
        return dispatchingServiceAndroidInjector;
    }

    @Override
    public AndroidInjector<Service> lollipopServiceInjector() {
        return appComponent.lollipopServiceComponent().injector();
    }

    @Override
    public AndroidInjector<BroadcastReceiver> broadcastReceiverInjector() {
        return dispatchingBroadcastAndroidInjector;
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public void saveDocument(String key, Document value) {
        documents.put(key, value);
        Timber.d("Document put into map");
    }
}
