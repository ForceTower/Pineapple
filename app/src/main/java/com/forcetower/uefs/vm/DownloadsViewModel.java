package com.forcetower.uefs.vm;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.rep.RefreshRepository;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.Status;

import javax.inject.Inject;

/**
 * Created by João Paulo on 23/03/2018.
 */

public class DownloadsViewModel extends ViewModel {
    private final RefreshRepository repository;

    private MediatorLiveData<Resource<Integer>> downloadCertificate;
    private boolean requestEnrollCertificate = false;

    @Inject
    public DownloadsViewModel(RefreshRepository repository) {
        this.repository = repository;
        downloadCertificate = new MediatorLiveData<>();
    }

    public MediatorLiveData<Resource<Integer>> getDownloadCertificate() {
        return downloadCertificate;
    }

    public void triggerDownloadCertificate() {
        if (!requestEnrollCertificate) {
            requestEnrollCertificate = true;
            LiveData<Resource<Integer>> downloadRes = repository.loginAndDownloadEnrollmentCertificate();
            downloadCertificate.addSource(downloadRes, resource -> {
                //noinspection ConstantConditions
                if (resource.status != Status.LOADING) {
                    downloadCertificate.removeSource(downloadRes);
                    requestEnrollCertificate = false;
                }
                downloadCertificate.postValue(resource);
            });
        }
    }
}
