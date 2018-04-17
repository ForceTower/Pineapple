package com.forcetower.uefs.di.module;

import com.forcetower.uefs.view.connected.fragments.AllSemestersGradeFragment;
import com.forcetower.uefs.view.connected.fragments.AutoSyncFragment;
import com.forcetower.uefs.view.connected.fragments.BigTrayFragment;
import com.forcetower.uefs.view.connected.fragments.CalendarFragment;
import com.forcetower.uefs.view.connected.fragments.ConnectedFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplineClassesFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplineDetailsFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplinesFragment;
import com.forcetower.uefs.view.connected.fragments.GradesFragment;
import com.forcetower.uefs.view.connected.fragments.MessagesFragment;
import com.forcetower.uefs.view.connected.fragments.NewScheduleFragment;
import com.forcetower.uefs.view.connected.fragments.OverviewFragment;
import com.forcetower.uefs.view.connected.fragments.ProfileFragment;
import com.forcetower.uefs.view.connected.fragments.ScheduleFragment;
import com.forcetower.uefs.view.connected.fragments.SemesterGradesFragment;
import com.forcetower.uefs.view.connected.fragments.TheAdventureFragment;
import com.forcetower.uefs.view.control_room.fragments.MasterSyncControlFragment;
import com.forcetower.uefs.view.login.fragment.ConnectingFragment;
import com.forcetower.uefs.view.login.fragment.LoginFormFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 05/03/2018.
 * Module binds fragments from activity, this allows fragments to receive injection as well
 */
@Module
public abstract class FragmentsModule {

    @ContributesAndroidInjector
    abstract LoginFormFragment contributeLoginFormFragment();

    @ContributesAndroidInjector
    abstract ConnectedFragment contributeConnectedFragment();

    @ContributesAndroidInjector
    abstract ConnectingFragment contributeConnectingFragment();

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
    abstract MasterSyncControlFragment contributeMasterSyncControlFragment();

    @ContributesAndroidInjector
    abstract AutoSyncFragment contributeAutoSyncFragment();

    @ContributesAndroidInjector
    abstract TheAdventureFragment contributeTheAdventureFragment();
}
