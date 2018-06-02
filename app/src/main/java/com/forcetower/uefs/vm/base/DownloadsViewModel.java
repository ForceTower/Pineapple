package com.forcetower.uefs.vm.base;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.SagresDocuments;
import com.forcetower.uefs.rep.helper.Status;
import com.forcetower.uefs.rep.sgrs.RefreshRepository;

import javax.inject.Inject;

/**
 * Created by João Paulo on 23/03/2018.
 */

public class DownloadsViewModel extends ViewModel {
    private final RefreshRepository repository;
    private final AppExecutors executors;

    private MediatorLiveData<Resource<Integer>> downloadCertificate;
    private MediatorLiveData<Resource<Integer>> downloadFlowchart;
    private boolean downloadingDocument = false;

    @Inject
    public DownloadsViewModel(RefreshRepository repository, AppExecutors executors) {
        this.repository = repository;
        this.executors = executors;
        downloadCertificate = new MediatorLiveData<>();
        downloadFlowchart = new MediatorLiveData<>();
    }

    public MediatorLiveData<Resource<Integer>> getDownloadCertificate() {
        return downloadCertificate;
    }

    public MediatorLiveData<Resource<Integer>> getDownloadFlowchart() {
        return downloadFlowchart;
    }

    public void triggerDownloadCertificate() {
        if (!downloadingDocument) {
            downloadingDocument = true;
            LiveData<Resource<Integer>> downloadRes = repository.loginAndDownloadPDFDocument(SagresDocuments.ENROLLMENT_CERTIFICATE);
            downloadCertificate.addSource(downloadRes, resource -> {
                //noinspection ConstantConditions
                if (resource.status != Status.LOADING) {
                    downloadCertificate.removeSource(downloadRes);
                    downloadingDocument = false;
                }
                downloadCertificate.postValue(resource);
            });
        }
    }

    public void triggerDownloadFlowchart() {
        if (!downloadingDocument) {
            downloadingDocument = true;
            LiveData<Resource<Integer>> downloadRes = repository.loginAndDownloadPDFDocument(SagresDocuments.FLOWCHART);
            downloadFlowchart.addSource(downloadRes, resource -> {
                //noinspection ConstantConditions
                if (resource.status != Status.LOADING) {
                    downloadFlowchart.removeSource(downloadRes);
                    downloadingDocument = false;
                }
                downloadFlowchart.postValue(resource);
            });
        }
    }

    public boolean isBusy() {
        return downloadingDocument;
    }

}
