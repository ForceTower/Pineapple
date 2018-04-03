package com.forcetower.uefs.rep;

import android.arch.lifecycle.LiveData;

import com.forcetower.uefs.service.ApiResponse;
import com.forcetower.uefs.service.UNEService;
import com.forcetower.uefs.service.Version;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by João Paulo on 01/04/2018.
 */
@Singleton
public class ServiceRepository {
    private final UNEService service;

    @Inject
    public ServiceRepository(UNEService service) {
        this.service = service;
    }

    public LiveData<ApiResponse<Version>> getUNESVersion() {
        return service.getLatestVersion();
    }
}