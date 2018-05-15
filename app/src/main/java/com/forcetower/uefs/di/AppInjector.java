package com.forcetower.uefs.di;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.di.component.DaggerAppComponent;

import dagger.android.AndroidInjection;
import dagger.android.support.AndroidSupportInjection;
import dagger.android.support.HasSupportFragmentInjector;

/**
 * Created by João Paulo on 05/03/2018.
 * Application Injector, this initialize the AppComponent and register the activity lifecycle callback
 * this is responsible for injecting into every single activity and fragment in the correct moment
 */
public class AppInjector  {
    public static void init(UEFSApplication application) {
        DaggerAppComponent.builder().application(application).build().inject(application);

        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                handleActivity(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {}

            @Override
            public void onActivityResumed(Activity activity) {}

            @Override
            public void onActivityPaused(Activity activity) {}

            @Override
            public void onActivityStopped(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

            @Override
            public void onActivityDestroyed(Activity activity) {}
        });
    }

    private static void handleActivity(Activity activity) {
        if (activity instanceof HasSupportFragmentInjector) {
            AndroidInjection.inject(activity);
        }

        if (activity instanceof androidx.fragment.app.FragmentActivity) {
            ((androidx.fragment.app.FragmentActivity)activity).getSupportFragmentManager()
                    .registerFragmentLifecycleCallbacks(new androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks() {
                        @Override
                        public void onFragmentCreated(androidx.fragment.app.FragmentManager fm, androidx.fragment.app.Fragment f, Bundle savedInstanceState) {
                            super.onFragmentCreated(fm, f, savedInstanceState);
                            if (f instanceof Injectable) {
                                AndroidSupportInjection.inject(f);
                            }
                        }
                    }, true);
        }


    }
}
