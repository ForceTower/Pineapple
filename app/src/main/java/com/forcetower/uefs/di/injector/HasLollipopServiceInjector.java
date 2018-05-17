package com.forcetower.uefs.di.injector;

import android.app.Service;

import dagger.android.AndroidInjector;

/**
 * Created by João Paulo on 17/05/2018.
 */
public interface HasLollipopServiceInjector {
    AndroidInjector<Service> lollipopServiceInjector();
}
