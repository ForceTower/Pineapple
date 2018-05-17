package com.forcetower.uefs.di.module;

import com.forcetower.uefs.svc.firebase.UNESFirebaseInstanceIDService;
import com.forcetower.uefs.svc.firebase.UNESFirebaseMessagingService;
import com.forcetower.uefs.worker.SagresSyncJobScheduler;
import com.forcetower.uefs.worker.SagresSyncJobService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 02/04/2018.
 */
@Module
public abstract class ServicesModule {
    @ContributesAndroidInjector
    abstract UNESFirebaseInstanceIDService contributeUNESFirebaseInstanceIDService();

    @ContributesAndroidInjector
    abstract UNESFirebaseMessagingService contributeUNESFirebaseMessagingService();

    @ContributesAndroidInjector
    abstract SagresSyncJobService contributeSagresSyncJobService();

    @ContributesAndroidInjector
    abstract SagresSyncJobScheduler contributeSagresSyncJobScheduler();
}
