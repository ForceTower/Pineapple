package com.forcetower.uefs.rep.resources;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.service.ApiResponse;

public abstract class SendNotificationResource {
    private final MediatorLiveData<Resource<ActionResult<String>>> result;
    private final AppExecutors executors;

    @MainThread
    protected SendNotificationResource(AppExecutors executors) {
        this.executors = executors;
        result = new MediatorLiveData<>();
        execute();
    }

    @MainThread
    private void execute() {
        LiveData<ApiResponse<ActionResult<String>>> call = createCall();
        result.addSource(call, response -> {
            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                result.postValue(Resource.success(response.body));
            } else {
                if (response.actionError != null) {
                    result.postValue(Resource.error(response.errorMessage, response.code, response.actionError));
                } else {
                    result.postValue(Resource.error(response.errorMessage, response.code, new Exception("Failed")));
                }
            }
        });
    }


    protected abstract LiveData<ApiResponse<ActionResult<String>>> createCall();

    public LiveData<Resource<ActionResult<String>>> asLiveData() {
        return result;
    }
}
