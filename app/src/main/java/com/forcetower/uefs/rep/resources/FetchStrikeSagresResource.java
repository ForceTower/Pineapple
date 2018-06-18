package com.forcetower.uefs.rep.resources;

import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.rep.helper.Resource;

/**
 * Created by João Paulo on 18/06/2018.
 */
public abstract class FetchStrikeSagresResource {
    private final AppExecutors executors;
    private final MediatorLiveData<Resource<Integer>> result;

    @MainThread
    public FetchStrikeSagresResource(AppExecutors executors) {
        this.executors = executors;
        this.result = new MediatorLiveData<>();

        result.postValue(Resource.loading(R.string.preparing_fetch));
    }
}
