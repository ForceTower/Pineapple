package com.forcetower.uefs.di.module;

import com.forcetower.uefs.view.connected.fragments.AllSemestersGradeFragment;
import com.forcetower.uefs.view.connected.fragments.BigTrayFragment;
import com.forcetower.uefs.view.connected.fragments.CalendarFragment;
import com.forcetower.uefs.view.connected.fragments.ConnectedFragment;
import com.forcetower.uefs.view.connected.fragments.CreateReminderFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplineClassesFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplineDetailsFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplinesFragment;
import com.forcetower.uefs.view.connected.fragments.EventCreationFourFragment;
import com.forcetower.uefs.view.connected.fragments.EventCreationOneFragment;
import com.forcetower.uefs.view.connected.fragments.EventCreationPreviewFragment;
import com.forcetower.uefs.view.connected.fragments.EventCreationStartFragment;
import com.forcetower.uefs.view.connected.fragments.EventCreationThreeFragment;
import com.forcetower.uefs.view.connected.fragments.EventCreationTwoFragment;
import com.forcetower.uefs.view.connected.fragments.EventsFragment;
import com.forcetower.uefs.view.connected.fragments.GradesFragment;
import com.forcetower.uefs.view.connected.fragments.MessagesFragment;
import com.forcetower.uefs.view.connected.fragments.NewScheduleFragment;
import com.forcetower.uefs.view.connected.fragments.OutdatedFragment;
import com.forcetower.uefs.view.connected.fragments.OverviewFragment;
import com.forcetower.uefs.view.connected.fragments.ProfileFragment;
import com.forcetower.uefs.view.connected.fragments.RemindersFragment;
import com.forcetower.uefs.view.connected.fragments.ScheduleFragment;
import com.forcetower.uefs.view.connected.fragments.SelectCourseFragment;
import com.forcetower.uefs.view.connected.fragments.SemesterGradesFragment;
import com.forcetower.uefs.view.connected.fragments.SyncRegistryFragment;
import com.forcetower.uefs.view.connected.fragments.TheAdventureFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 05/03/2018.
 * Module binds fragments from activity, this allows fragments to receive injection as well
 */
@SuppressWarnings({"Unused", "WeakerAccess"})
@Module
public abstract class FragmentsModule {

    @ContributesAndroidInjector
    abstract ConnectedFragment contributeConnectedFragment();

    @ContributesAndroidInjector
    abstract ScheduleFragment contributeScheduleFragment();

    @ContributesAndroidInjector
    abstract MessagesFragment contributeMessagesFragment();

    @ContributesAndroidInjector
    abstract DisciplinesFragment contributeDisciplinesFragment();

    @ContributesAndroidInjector
    abstract AllSemestersGradeFragment contributeAllSemestersGradeFragment();

    @ContributesAndroidInjector
    abstract SemesterGradesFragment contributeSemesterGradesFragment();

    @ContributesAndroidInjector
    abstract ProfileFragment contributeProfileFragment();

    @ContributesAndroidInjector
    abstract CalendarFragment contributeCalendarFragment();

    @ContributesAndroidInjector
    abstract OverviewFragment contributeOverviewFragment();

    @ContributesAndroidInjector
    abstract GradesFragment contributeGradesFragment();

    @ContributesAndroidInjector
    abstract NewScheduleFragment contributeNewScheduleFragment();

    @ContributesAndroidInjector
    abstract BigTrayFragment contributeBigTrayFragment();

    @ContributesAndroidInjector
    abstract DisciplineDetailsFragment contributeDisciplineDetailsFragment();

    @ContributesAndroidInjector
    abstract DisciplineClassesFragment contributeDisciplineClassesFragment();

    @ContributesAndroidInjector
    abstract OutdatedFragment contributeAutoSyncFragment();

    @ContributesAndroidInjector
    abstract TheAdventureFragment contributeTheAdventureFragment();

    @ContributesAndroidInjector
    abstract EventsFragment contributeEventsFragment();

    @ContributesAndroidInjector
    abstract EventCreationStartFragment contributeEventCreationStart();

    @ContributesAndroidInjector
    abstract EventCreationOneFragment contributeEventCreationOneFragment();

    @ContributesAndroidInjector
    abstract EventCreationTwoFragment contributeEventCreationTwoFragment();

    @ContributesAndroidInjector
    abstract EventCreationThreeFragment contributeEventCreationThreeFragment();

    @ContributesAndroidInjector
    abstract EventCreationFourFragment contributeEventCreationFourFragment();

    @ContributesAndroidInjector
    abstract EventCreationPreviewFragment contributeEventCreationPreviewFragment();

    @ContributesAndroidInjector
    abstract SelectCourseFragment contributeSelectCourseFragment();

    @ContributesAndroidInjector
    abstract RemindersFragment contributeRemindersFragment();

    @ContributesAndroidInjector
    abstract CreateReminderFragment contributeCreateReminderFragment();

    @ContributesAndroidInjector
    abstract SyncRegistryFragment contributeSyncRegistryFragment();
}
