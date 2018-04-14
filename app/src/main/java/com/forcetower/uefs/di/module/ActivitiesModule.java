package com.forcetower.uefs.di.module;

import com.forcetower.uefs.view.about.AboutActivity;
import com.forcetower.uefs.view.connected.LoggedActivity;
import com.forcetower.uefs.view.connected.fragments.ConnectedFragment;
import com.forcetower.uefs.view.control_room.ControlRoomActivity;
import com.forcetower.uefs.view.discipline.DisciplineClassesActivity;
import com.forcetower.uefs.view.login.MainActivity;
import com.forcetower.uefs.view.settings.SettingsActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 05/03/2018.
 * Module binds Contexts to Android Injector, thus allowing "automatic" injection
 */
@Module
public abstract class ActivitiesModule {
    @ContributesAndroidInjector(modules = FragmentsModule.class)
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector(modules = FragmentsModule.class)
    abstract ConnectedFragment contributeConnectedActivity();

    @ContributesAndroidInjector
    abstract SettingsActivity contributeSettingsActivity();

    @ContributesAndroidInjector
    abstract DisciplineClassesActivity contributeDisciplineClassesActivity();

    @ContributesAndroidInjector(modules = FragmentsModule.class)
    abstract ControlRoomActivity contributeControlRoomActivity();

    @ContributesAndroidInjector
    abstract AboutActivity contributeAboutActivity();

    @ContributesAndroidInjector(modules = FragmentsModule.class)
    abstract LoggedActivity contributeLoggedActivity();
}
