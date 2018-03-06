package com.forcetower.uefs.dependency_injection.component;

import android.app.Application;

import com.forcetower.uefs.dependency_injection.module.ApplicationModule;
import com.forcetower.uefs.dependency_injection.module.RoomModule;
import com.forcetower.uefs.dependency_injection.module.ServicesModule;
import com.forcetower.uefs.services.tasks.MigrateToLocalDatabaseTask;
import com.forcetower.uefs.view.class_details.ClassDetailsActivity;
import com.forcetower.uefs.view.class_details.CreateTodoItemDialog;
import com.forcetower.uefs.view.class_details.TodoListFragment;
import com.forcetower.uefs.view.connected.CalendarFragment;
import com.forcetower.uefs.view.connected.ConnectedActivity;
import com.forcetower.uefs.view.connected.NConnectedActivity;
import com.forcetower.uefs.view.experiments.class_reviews.ClassReviewActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Singleton
@Component(modules = {ApplicationModule.class, RoomModule.class, ServicesModule.class})
public interface ApplicationComponent {
    void inject(ConnectedActivity activity);
    void inject(NConnectedActivity activity);
    void inject(ClassDetailsActivity activity);
    void inject(MigrateToLocalDatabaseTask task);
    void inject(CalendarFragment fragment);
    void inject(TodoListFragment fragment);
    void inject(CreateTodoItemDialog dialog);
    void inject(ClassReviewActivity activity);

    Application application();
}
