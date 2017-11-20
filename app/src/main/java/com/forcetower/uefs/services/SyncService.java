package com.forcetower.uefs.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.forcetower.uefs.adapters.sync.MessagesAdapter;

/**
 * Created by João Paulo on 20/11/2017.
 */

public class SyncService extends Service{
    private static final Object LOCK = new Object();
    private static MessagesAdapter syncAdapter = null;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (LOCK) {
            if (syncAdapter == null)
                syncAdapter = new MessagesAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return syncAdapter.getSyncAdapterBinder();
    }
}
