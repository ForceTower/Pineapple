package com.forcetower.uefs.rep.resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.rep.helper.SagresDocuments;
import com.forcetower.uefs.sgrs.SagresResponse;
import com.forcetower.uefs.util.network.LiveDataCallAdapter;

import org.jsoup.nodes.Document;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Created by João Paulo on 23/03/2018.
 */

public abstract class FetchEnrollCertResource {
    private final MediatorLiveData<Resource<Integer>> result;
    private final AppExecutors executors;

    @MainThread
    public FetchEnrollCertResource(AppExecutors executors) {
        this.executors = executors;
        this.result = new MediatorLiveData<>();
        Timber.d("Start fetch of enroll");
        result.setValue(Resource.loading(R.string.going_to_enrollment_page));

        fetchFromSagres();
    }

    private void fetchFromSagres() {
        Call call = createCertificateCall();
        LiveData<SagresResponse> gradesRsp = LiveDataCallAdapter.adapt(call);
        result.addSource(gradesRsp, response -> {
            result.removeSource(gradesRsp);
            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                Document document = response.getDocument();
                executors.diskIO().execute(() -> {
                    String link = findDocumentLink(document);
                    if (link == null) {
                        result.postValue(Resource.error("link is invalid", R.string.link_to_resource_not_found));
                    } else {
                        Call downloadCall = createDownloadCall(link);
                        executors.networkIO().execute(() -> {
                            try {
                                Response downResp = downloadCall.execute();
                                if (downResp.isSuccessful() && downloadFile(downResp)) {
                                    result.postValue(Resource.success(R.string.completed));
                                } else {
                                    result.postValue(Resource.error(response.getMessage(), R.string.failed_to_connect));
                                }
                            } catch (IOException e) {
                                result.postValue(Resource.error(response.getMessage(), R.string.failed_to_connect));
                            }
                        });
                    }
                });
            } else {
                result.postValue(Resource.error(response.getMessage(), R.string.failed_to_connect));
            }
        });
    }

    protected abstract boolean downloadFile(Response downResp);
    protected abstract Call createDownloadCall(String link);
    protected abstract String findDocumentLink(@NonNull Document document);
    protected abstract Call createCertificateCall();

    public LiveData<Resource<Integer>> asLiveData() {
        return result;
    }
}
