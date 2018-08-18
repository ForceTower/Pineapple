package com.forcetower.uefs.di.component;

import android.app.Application;

import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.di.module.ActivitiesModule;
import com.forcetower.uefs.di.module.AppModule;
import com.forcetower.uefs.di.module.DatabaseModule;
import com.forcetower.uefs.di.module.DatabaseServiceModule;
import com.forcetower.uefs.di.module.NetworkModule;
import com.forcetower.uefs.di.module.ReceiversModule;
import com.forcetower.uefs.di.module.ServicesModule;
import com.forcetower.uefs.work.event.CreateEventWorker;
import com.forcetower.uefs.work.event.EventApprovalWorker;
import com.forcetower.uefs.work.grades.DownloadGradesWorker;
import com.forcetower.uefs.work.sync.SagresSyncWorker;
import com.forcetower.uefs.work.sync.SyncJobScheduler;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Created by João Paulo on 05/03/2018.
 * Application module, this is the kick start
 */
@Singleton
@Component(modules = {
        AndroidInjectionModule.class,
        AndroidSupportInjectionModule.class,
        AppModule.class,
        DatabaseModule.class,
        NetworkModule.class,
        ActivitiesModule.class,
        ServicesModule.class,
        DatabaseServiceModule.class,
        ReceiversModule.class
})
public interface AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance Builder application(Application application);
        AppComponent build();
    }

    void inject(UEFSApplication application);

    void inject(SagresSyncWorker worker);
    void inject(CreateEventWorker worker);
    void inject(EventApprovalWorker worker);
    void inject(DownloadGradesWorker worker);

    LollipopGreaterServiceComponent lollipopServiceComponent();
}
