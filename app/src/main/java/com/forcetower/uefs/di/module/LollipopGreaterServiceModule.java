package com.forcetower.uefs.di.module;

import com.forcetower.uefs.work.sync.SyncJobScheduler;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 17/05/2018.
 */
@Module
public abstract class LollipopGreaterServiceModule {
    @ContributesAndroidInjector
    abstract SyncJobScheduler contributeSyncJobScheduler();
}
