package com.forcetower.uefs.di.module;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.forcetower.uefs.di.annotation.ViewModelKey;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.admin.ControlRoomViewModel;
import com.forcetower.uefs.vm.base.CalendarViewModel;
import com.forcetower.uefs.vm.base.DisciplinesViewModel;
import com.forcetower.uefs.vm.base.DownloadsViewModel;
import com.forcetower.uefs.vm.base.GradesViewModel;
import com.forcetower.uefs.vm.base.LoginViewModel;
import com.forcetower.uefs.vm.base.MessagesViewModel;
import com.forcetower.uefs.vm.base.ProfileViewModel;
import com.forcetower.uefs.vm.base.ScheduleViewModel;
import com.forcetower.uefs.vm.base.TodoItemViewModel;
import com.forcetower.uefs.vm.google.AchievementsViewModel;
import com.forcetower.uefs.vm.google.GoogleCalendarViewModel;
import com.forcetower.uefs.vm.service.EventsViewModel;
import com.forcetower.uefs.vm.service.ServiceGeneralViewModel;
import com.forcetower.uefs.vm.universe.UAccountViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by João Paulo on 05/03/2018.
 * Binds all view models into the factory so it can be created in Activities/Fragments etc
 */
@SuppressWarnings("WeakerAccess")
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
    @IntoMap
    @ViewModelKey(AchievementsViewModel.class)
    abstract ViewModel bindAchievementsViewModel(AchievementsViewModel achievementsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UAccountViewModel.class)
    abstract ViewModel bindUAccountViewModel(UAccountViewModel accountViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ServiceGeneralViewModel.class)
    abstract ViewModel bindServiceGeneralViewModel(ServiceGeneralViewModel serviceGeneralViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EventsViewModel.class)
    abstract ViewModel bindEventsViewModel(EventsViewModel eventsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(TodoItemViewModel.class)
    abstract ViewModel bindTodoItemViewModel(TodoItemViewModel todoItemViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(UEFSViewModelFactory factory);
}
