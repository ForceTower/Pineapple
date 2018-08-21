package com.forcetower.uefs.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * Created by João Paulo on 08/03/2018.
 */

public class SyncAdapterService extends Service {
    @Inject
    SagresSyncAdapter syncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}