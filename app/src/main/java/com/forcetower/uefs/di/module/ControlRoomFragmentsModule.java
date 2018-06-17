package com.forcetower.uefs.di.module;

import com.forcetower.uefs.view.control_room.fragments.MasterSyncControlFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Created by João Paulo on 16/06/2018.
 */
@Module
public abstract class ControlRoomFragmentsModule {
    @ContributesAndroidInjector
    abstract MasterSyncControlFragment contributeMasterSyncControlFragment();
}