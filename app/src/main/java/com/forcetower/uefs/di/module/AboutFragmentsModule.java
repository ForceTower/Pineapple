package com.forcetower.uefs.di.module;

import com.forcetower.uefs.view.about.fragments.AboutFragment;
import com.forcetower.uefs.view.about.fragments.FAQFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 03/06/2018.
 */
@Module
public abstract class AboutFragmentsModule {
    @ContributesAndroidInjector
    abstract AboutFragment contributeAboutFragment();

    @ContributesAndroidInjector
    abstract FAQFragment contributeFAQFragment();
}
