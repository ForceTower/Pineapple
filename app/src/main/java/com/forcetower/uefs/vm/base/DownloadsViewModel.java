package com.forcetower.uefs.vm.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

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
    private MediatorLiveData<Resource<Integer>> downloadHistory;
    private boolean downloadingDocument = false;

    @Inject
    public DownloadsViewModel(RefreshRepository repository, AppExecutors executors) {
        this.repository = repository;
        this.executors = executors;
        downloadCertificate = new MediatorLiveData<>();
        downloadFlowchart = new MediatorLiveData<>();
        downloadHistory = new MediatorLiveData<>();
    }

    public LiveData<Resource<Integer>> getDownloadCertificate() {
        return downloadCertificate;
    }

    public LiveData<Resource<Integer>> getDownloadFlowchart() {
        return downloadFlowchart;
    }

    public LiveData<Resource<Integer>> getDownloadHistory() {
        return downloadHistory;
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

    public void triggerDownloadSchoolHistory() {
        if (!downloadingDocument) {
            downloadingDocument = true;
            LiveData<Resource<Integer>> downloadRes = repository.loginAndDownloadPDFDocument(SagresDocuments.SCHOLAR_HISTORY);
            downloadHistory.addSource(downloadRes, resource -> {
                //noinspection ConstantConditions
                if (resource.status != Status.LOADING) {
                    downloadHistory.removeSource(downloadRes);
                    downloadingDocument = false;
                }
                downloadHistory.postValue(resource);
            });
        }
    }
}
