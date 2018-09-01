package com.forcetower.uefs.di.module;

import com.forcetower.uefs.feature.siecomp.SessionDetailsActivity;
import com.forcetower.uefs.feature.siecomp.SiecompActivity;
import com.forcetower.uefs.feature.siecomp.SpeakerActivity;
import com.forcetower.uefs.game.g2048.activity.Game2048Activity;
import com.forcetower.uefs.view.about.AboutActivity;
import com.forcetower.uefs.view.connected.LoggedActivity;
import com.forcetower.uefs.view.control_room.ControlRoomActivity;
import com.forcetower.uefs.view.event.EventDetailsActivity;
import com.forcetower.uefs.view.login.MainActivity;
import com.forcetower.uefs.view.settings.SettingsActivity;
import com.forcetower.uefs.view.universe.UniverseActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 05/03/2018.
 * Module binds Contexts to Android Injector, thus allowing "automatic" injection
 */
@Module
public abstract class ActivitiesModule {
    @ContributesAndroidInjector(modules = MainActivityFragmentsModule.class)
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract SettingsActivity contributeSettingsActivity();

    @ContributesAndroidInjector(modules = ControlRoomFragmentsModule.class)
    abstract ControlRoomActivity contributeControlRoomActivity();

    @ContributesAndroidInjector(modules = AboutFragmentsModule.class)
    abstract AboutActivity contributeAboutActivity();

    @ContributesAndroidInjector(modules = FragmentsModule.class)
    abstract LoggedActivity contributeLoggedActivity();

    @ContributesAndroidInjector(modules = UniverseFragmentsModule.class)
    abstract UniverseActivity contributeUniverseActivity();

    @ContributesAndroidInjector
    abstract Game2048Activity contributeGame2048Activity();

    @ContributesAndroidInjector
    abstract EventDetailsActivity contributeEventDetailsActivity();

    @ContributesAndroidInjector(modules = SiecompModule.class)
    abstract SiecompActivity contributeSiecompActivity();

    @ContributesAndroidInjector(modules = SessionDetailsModule.class)
    abstract SessionDetailsActivity contributesSessionDetailsActivity();

    @ContributesAndroidInjector(modules = SpeakerModule.class)
    abstract SpeakerActivity contributesSpeakerActivity();
}
