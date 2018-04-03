package com.forcetower.uefs.di.module;

import com.forcetower.uefs.svc.firebase.UNESFirebaseInstanceIDService;
import com.forcetower.uefs.svc.firebase.UNESFirebaseMessagingService;
import com.forcetower.uefs.sync.SyncAdapterService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 02/04/2018.
 */
@Module
public abstract class ServicesModule {
    @ContributesAndroidInjector
    abstract SyncAdapterService contributeSyncAdapterService();

    @ContributesAndroidInjector
    abstract UNESFirebaseInstanceIDService contributeUNESFirebaseInstanceIDService();

    @ContributesAndroidInjector
    abstract UNESFirebaseMessagingService contributeUNESFirebaseMessagingService();
}
