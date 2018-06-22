package com.forcetower.uefs.di.module;

import com.forcetower.uefs.view.universe.fragment.UniverseCreateAccountFragment;
import com.forcetower.uefs.view.universe.fragment.UniverseKnowAboutFragment;
import com.forcetower.uefs.view.universe.fragment.UniverseLoginFragment;
import com.forcetower.uefs.view.universe.fragment.UniverseTokenVerifyFragment;
import com.forcetower.uefs.view.universe.fragment.UniverseWelcomeStartFragment;
import com.forcetower.uefs.view.universe.fragment.YouAreReadyFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 11/05/2018.
 */
@SuppressWarnings("WeakerAccess")
@Module
public abstract class UniverseFragmentsModule {
    @ContributesAndroidInjector
    abstract UniverseTokenVerifyFragment contributeUniverseTokenVerifyFragment();

    @ContributesAndroidInjector
    abstract UniverseWelcomeStartFragment contributeUniverseWelcomeStartFragment();

    @ContributesAndroidInjector
    abstract UniverseCreateAccountFragment contributeUniverseCreateAccountFragment();

    @ContributesAndroidInjector
    abstract UniverseLoginFragment contributeUniverseLoginFragment();

    @ContributesAndroidInjector
    abstract YouAreReadyFragment contributeYouAreReadyFragment();

    @ContributesAndroidInjector
    abstract UniverseKnowAboutFragment contributeUniverseKnowAboutFragment();
}
