package com.forcetower.uefs.di.module;

import com.forcetower.uefs.view.connected.fragments.AllSemestersGradeFragment;
import com.forcetower.uefs.view.connected.fragments.CalendarFragment;
import com.forcetower.uefs.view.connected.fragments.DisciplinesFragment;
import com.forcetower.uefs.view.connected.fragments.MessagesFragment;
import com.forcetower.uefs.view.connected.fragments.ProfileFragment;
import com.forcetower.uefs.view.connected.fragments.ScheduleFragment;
import com.forcetower.uefs.view.connected.fragments.SemesterGradesFragment;
import com.forcetower.uefs.view.control_room.fragments.MasterSyncControlFragment;
import com.forcetower.uefs.view.discipline.fragments.GradesFragment;
import com.forcetower.uefs.view.discipline.fragments.OverviewFragment;
import com.forcetower.uefs.view.experimental.good_barrel.fragments.BarrelFragment;
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
    abstract MasterSyncControlFragment contributeMasterSyncControlFragment();

    @ContributesAndroidInjector
    abstract BarrelFragment contributeBarrelFragment();
}
