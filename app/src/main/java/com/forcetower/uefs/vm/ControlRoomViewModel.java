package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.SimpleResponse;
import com.forcetower.uefs.service.SyncResponse;
import com.forcetower.uefs.service.UNEService;
import com.forcetower.uefs.util.AbsentLiveData;

import javax.inject.Inject;

/**
 * Created by João Paulo on 13/03/2018.
 */

public class ControlRoomViewModel extends ViewModel {
    private final UNEService service;

    private LiveData<ApiResponse<SimpleResponse>> updateState;
    private LiveData<ApiResponse<SyncResponse>> syncState;

    private boolean fragmentCall = false;

    @Inject
    ControlRoomViewModel(UNEService service) {
        this.service = service;
        updateState = AbsentLiveData.create();
        syncState = AbsentLiveData.create();
    }

    public LiveData<ApiResponse<SimpleResponse>> updateMasterSyncState(boolean state, String password) {
        updateState = service.setupMasterSync(state, password);
        return updateState;
    }

    public LiveData<ApiResponse<SimpleResponse>> updateMasterSyncState() {
        return updateState;
    }

    public LiveData<ApiResponse<SyncResponse>> observeCurrentState() {
        return syncState;
    }

    public LiveData<ApiResponse<SyncResponse>> getCurrentState() {
        syncState = service.getSyncState();
        return syncState;
    }

    public boolean getFragmentCall() {
        return fragmentCall;
    }

    public void setFragmentCall(boolean fragmentCall) {
        this.fragmentCall = fragmentCall;
    }
}
