package com.forcetower.uefs.di.module;

import com.forcetower.uefs.rcv.OnUpgradeBroadcastReceiver;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 02/04/2018.
 */
@Module
public abstract class ReceiversModule {
    @ContributesAndroidInjector
    abstract OnUpgradeBroadcastReceiver contributeOnUpgradeBroadcastReceiver();
}
