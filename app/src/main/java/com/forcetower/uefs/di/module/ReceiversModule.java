package com.forcetower.uefs.di.module;

import com.forcetower.uefs.alm.RefreshBroadcastReceiver;
import com.forcetower.uefs.rcv.UpdateReceiver;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 02/04/2018.
 */
@Module
public abstract class ReceiversModule {
    @ContributesAndroidInjector
    abstract UpdateReceiver contributeUpdateReceiver();
    @ContributesAndroidInjector
    abstract RefreshBroadcastReceiver contributeRefreshBroadcastReceiver();
}
