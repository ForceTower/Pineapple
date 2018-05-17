package com.forcetower.uefs.di.component;

import android.app.Service;

import com.forcetower.uefs.di.module.LollipopGreaterServiceModule;

import dagger.Subcomponent;
import dagger.android.DispatchingAndroidInjector;

/**
 * Created by João Paulo on 17/05/2018.
 */
@Subcomponent(modules = LollipopGreaterServiceModule.class)
public interface LollipopGreaterServiceComponent {
    DispatchingAndroidInjector<Service> injector();
}
