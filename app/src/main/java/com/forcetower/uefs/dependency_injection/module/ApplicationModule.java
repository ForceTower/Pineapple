package com.forcetower.uefs.dependency_injection.module;

import android.app.Application;

import com.forcetower.uefs.UEFSApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by João Paulo on 29/12/2017.
 */

@Module
public class ApplicationModule {
    private final UEFSApplication application;

    public ApplicationModule(UEFSApplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    UEFSApplication provideUEFSApplication() {
        return application;
    }

    @Provides
    @Singleton
    Application provideApplication() {
        return application;
    }
}
