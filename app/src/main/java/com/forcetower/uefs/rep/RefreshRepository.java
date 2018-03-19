package com.forcetower.uefs.rep;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;

import javax.inject.Inject;
import javax.inject.Singleton;

import timber.log.Timber;

/**
 * Created by João Paulo on 08/03/2018.
 */
@Singleton
public class RefreshRepository {
    private final AppDatabase database;
    private final AppExecutors executors;
    private final LoginRepository loginRepository;

    @Inject
    RefreshRepository(AppDatabase database, AppExecutors executors, LoginRepository loginRepository) {
        this.database = database;
        this.executors = executors;
        this.loginRepository = loginRepository;
    }

    public LiveData<Resource<Integer>> refreshData() {
        MediatorLiveData<Resource<Integer>> refresh = new MediatorLiveData<>();
        LiveData<Access> accessSrc = database.accessDao().getAccess();
        refresh.addSource(accessSrc, access -> {
            refresh.removeSource(accessSrc);
            //noinspection ConstantConditions
            LiveData<Resource<Integer>> loginRes = loginRepository.login(access.getUsername(), access.getPassword());
            refresh.addSource(loginRes, integerResource -> {
                //noinspection ConstantConditions
                if (integerResource.status == Status.SUCCESS) {
                    refresh.removeSource(loginRes);
                    refresh.postValue(Resource.success(R.string.updated));
                } else if (integerResource.status == Status.ERROR) {
                    refresh.removeSource(loginRes);
                    int code = integerResource.code;
                    if (code == 401) {
                        Timber.d("User disconnected");
                        //Disconnects the user because validation failed
                        executors.diskIO().execute(() -> database.accessDao().deleteAllAccesses());
                    }
                } else {
                    refresh.postValue(integerResource);
                }
            });
        });
        return refresh;
    }
}
