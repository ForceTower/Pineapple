package com.forcetower.uefs.rep.resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.sgrs.SagresResponse;
import com.forcetower.uefs.util.network.LiveDataCallAdapter;

import org.jsoup.nodes.Document;

import okhttp3.Call;
import timber.log.Timber;

/**
 * Created by João Paulo on 06/03/2018.
 */

public abstract class FetchGradesResource {
    private final MediatorLiveData<Resource<Integer>> result;
    private final AppExecutors executors;

    @MainThread
    public FetchGradesResource(AppExecutors executors) {
        this.executors = executors;
        result = new MediatorLiveData<>();
        Timber.d("Fetch grades called");
        result.setValue(Resource.loading(R.string.going_to_grades_page));

        fetchFromSagres();
    }

    private void fetchFromSagres() {
        Call call = createGradesCall();
        LiveData<SagresResponse> gradesResponse = LiveDataCallAdapter.adapt(call);
        result.addSource(gradesResponse, response -> {
            result.removeSource(gradesResponse);
            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                Document document = response.getDocument();
                result.postValue(Resource.loading(R.string.processing_information));
                executors.diskIO().execute(() -> {
                    saveResult(document);
                    executors.mainThread().execute(() -> result.setValue(Resource.success(R.string.grades_obtained)));
                });
            } else {
                result.postValue(Resource.error(response.getMessage(), response.getCode(), R.string.failed_to_connect));
            }
        });
    }

    public LiveData<Resource<Integer>> asLiveData() {
        return result;
    }

    public abstract Call createGradesCall();
    public abstract void saveResult(@NonNull Document document);
}
