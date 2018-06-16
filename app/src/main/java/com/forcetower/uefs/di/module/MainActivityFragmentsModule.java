package com.forcetower.uefs.di.module;

import com.forcetower.uefs.view.login.fragment.ConnectingFragment;
import com.forcetower.uefs.view.login.fragment.LoginFormFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 16/06/2018.
 */
@Module
public abstract class MainActivityFragmentsModule {
    @ContributesAndroidInjector
    abstract LoginFormFragment contributeLoginFormFragment();

    @ContributesAndroidInjector
    abstract ConnectingFragment contributeConnectingFragment();
}
