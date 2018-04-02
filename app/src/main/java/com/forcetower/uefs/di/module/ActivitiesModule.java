package com.forcetower.uefs.di.module;

import com.forcetower.uefs.alm.RefreshBroadcastReceiver;
import com.forcetower.uefs.sync.SyncAdapterService;
import com.forcetower.uefs.view.connected.ConnectedActivity;
import com.forcetower.uefs.view.control_room.ControlRoomActivity;
import com.forcetower.uefs.view.discipline.DisciplineClassesActivity;
import com.forcetower.uefs.view.discipline.DisciplineDetailsActivity;
import com.forcetower.uefs.view.experimental.good_barrel.GoodBarrelActivity;
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
    abstract ConnectedActivity contributeConnectedActivity();

    @ContributesAndroidInjector
    abstract SettingsActivity contributeSettingsActivity();

    @ContributesAndroidInjector(modules = FragmentsModule.class)
    abstract DisciplineDetailsActivity contributeDisciplineDetails();

    @ContributesAndroidInjector
    abstract DisciplineClassesActivity contributeDisciplineClassesActivity();

    @ContributesAndroidInjector(modules = FragmentsModule.class)
    abstract ControlRoomActivity contributeControlRoomActivity();

    @ContributesAndroidInjector(modules = FragmentsModule.class)
    abstract GoodBarrelActivity contributeGoodBarrelActivity();
}
