package com.forcetower.uefs;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.di.AppInjector;
import com.forcetower.uefs.di.component.AppComponent;
import com.forcetower.uefs.di.injector.HasLollipopServiceInjector;
import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.ntf.NotificationHelper;
import com.forcetower.uefs.rep.sgrs.RefreshRepository;
import com.forcetower.uefs.service.UNEService;
import com.forcetower.uefs.sync.service.SyncConfiguration;
import com.forcetower.uefs.worker.SyncUtils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.Picasso;

import java.io.File;

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
    @Inject
    FirebaseJobDispatcher dispatcher;

    private AppComponent appComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this))
            return;

        LeakCanary.install(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        appComponent = AppInjector.init(this);

        configureFeatures();

        Picasso.Builder builder = new Picasso.Builder(this);
        Picasso.setSingletonInstance(builder.build());

        showNewVersionNotification();
    }

    private void showNewVersionNotification() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("unes_not_connected_notification_v6.0.0", true)) {
            boolean not = NotificationCreator.notConnectedNotification(this);
            preferences.edit().putBoolean("unes_not_connected_notification_v6.0.0", !not).apply();
        }
    }

    private void configureFeatures() {
        new NotificationHelper(this).createChannels();
        String strFrequency = PreferenceManager.getDefaultSharedPreferences(this).getString("sync_frequency", "60");
        int frequency = 60;
        try {
            frequency = Integer.parseInt(strFrequency);
        } catch (Exception ignored) {}
        SyncUtils.setupSagresSync(dispatcher, this, frequency);
        SyncConfiguration.initializeSyncAdapter(this);
    }

    public void clearApplicationData() {
        File cacheDirectory = getCacheDir();
        File applicationDirectory = new File(cacheDirectory.getParent());
        if (applicationDirectory.exists()) {
            String[] fileNames = applicationDirectory.list();
            for (String fileName : fileNames) {
                if (!fileName.equals("lib")) {
                    deleteFile(new File(applicationDirectory, fileName));
                }
            }
        }
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
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

    //TODO Temporary code until WorkInjector is created
    @Inject
    RefreshRepository repository;
    @Inject
    AppDatabase database;
    @Inject
    AppExecutors executors;
    @Inject
    UNEService service;
    public RefreshObjects getRefreshPackage() {
        return new RefreshObjects(repository, database, executors, service);
    }

    public static final class RefreshObjects {
        public final RefreshRepository repository;
        public final AppDatabase database;
        public final AppExecutors executors;
        public final UNEService service;

        RefreshObjects(RefreshRepository repository, AppDatabase database, AppExecutors executors, UNEService service) {
            this.repository = repository;
            this.database = database;
            this.executors = executors;
            this.service = service;
        }
    }
}
