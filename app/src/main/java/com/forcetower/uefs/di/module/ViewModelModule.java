package com.forcetower.uefs.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.forcetower.uefs.di.annotation.ViewModelKey;
import com.forcetower.uefs.vm.CalendarViewModel;
import com.forcetower.uefs.vm.ControlRoomViewModel;
import com.forcetower.uefs.vm.DisciplinesViewModel;
import com.forcetower.uefs.vm.DownloadsViewModel;
import com.forcetower.uefs.vm.GoogleCalendarViewModel;
import com.forcetower.uefs.vm.GradesViewModel;
import com.forcetower.uefs.vm.LoginViewModel;
import com.forcetower.uefs.vm.MessagesViewModel;
import com.forcetower.uefs.vm.ProfileViewModel;
import com.forcetower.uefs.vm.ScheduleViewModel;
import com.forcetower.uefs.vm.UEFSViewModelFactory;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by João Paulo on 05/03/2018.
 * Binds all view models into the factory so it can be created in Activities/Fragments etc
 */
@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ScheduleViewModel.class)
    abstract ViewModel bindScheduleViewModel(ScheduleViewModel scheduleViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(MessagesViewModel.class)
    abstract ViewModel bindMessagesViewModel(MessagesViewModel messagesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DisciplinesViewModel.class)
    abstract ViewModel bindDisciplinesViewModel(DisciplinesViewModel disciplinesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(GradesViewModel.class)
    abstract ViewModel bindGradesViewModel(GradesViewModel gradesViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel bindProfileViewModel(ProfileViewModel profileViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CalendarViewModel.class)
    abstract ViewModel bindCalendarViewModel(CalendarViewModel calendarViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ControlRoomViewModel.class)
    abstract ViewModel bindControlRoomViewModel(ControlRoomViewModel controlRoomViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DownloadsViewModel.class)
    abstract ViewModel bindDownloadsViewModel(DownloadsViewModel downloadsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(GoogleCalendarViewModel.class)
    abstract ViewModel bindGoogleCalendarViewModel(GoogleCalendarViewModel googleCalendarViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(UEFSViewModelFactory factory);
}
