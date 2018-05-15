package com.forcetower.uefs.vm.admin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.forcetower.uefs.db_service.entity.UpdateStatus;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.UNEService;
import com.forcetower.uefs.util.AbsentLiveData;

import javax.inject.Inject;

/**
 * Created by João Paulo on 13/03/2018.
 */

public class ControlRoomViewModel extends ViewModel {
    private final UNEService service;

    private LiveData<ApiResponse<ActionResult<UpdateStatus>>> updateState;
    private LiveData<ApiResponse<UpdateStatus>> syncState;

    private boolean fragmentCall = false;

    @Inject
    ControlRoomViewModel(UNEService service) {
        this.service = service;
        updateState = AbsentLiveData.create();
        syncState = AbsentLiveData.create();
    }

    public LiveData<ApiResponse<ActionResult<UpdateStatus>>> updateMasterSyncState(boolean manager, boolean alarm) {
        int m = manager ? 1 : 0;
        int a = alarm   ? 1 : 0;
        updateState = service.changeUpdateStatus(m, a);
        return updateState;
    }

    public LiveData<ApiResponse<ActionResult<UpdateStatus>>> updateMasterSyncState() {
        return updateState;
    }

    public LiveData<ApiResponse<UpdateStatus>> observeCurrentState() {
        return syncState;
    }

    public LiveData<ApiResponse<UpdateStatus>> getCurrentState() {
        syncState = service.getUpdateStatus();
        return syncState;
    }

    public boolean getFragmentCall() {
        return fragmentCall;
    }

    public void setFragmentCall(boolean fragmentCall) {
        this.fragmentCall = fragmentCall;
    }
}
