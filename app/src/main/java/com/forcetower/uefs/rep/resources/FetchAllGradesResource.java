package com.forcetower.uefs.rep.resources;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.R;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.sgrs.SagresResponse;
import com.forcetower.uefs.util.network.LiveDataCallAdapter;

import org.jsoup.nodes.Document;

import java.util.List;

import okhttp3.Call;
import timber.log.Timber;

/**
 * Created by João Paulo on 07/03/2018.
 */

@SuppressWarnings("WeakerAccess")
public abstract class FetchAllGradesResource {
    private final MediatorLiveData<Resource<Integer>> result;
    private final AppExecutors executors;

    @MainThread
    public FetchAllGradesResource(AppExecutors executors) {
        this.executors = executors;
        this.result = new MediatorLiveData<>();
        Timber.d("Start fetch of all grades");
        result.setValue(Resource.loading(R.string.going_to_grades_page));

        fetchFromSagres();
    }

    private void fetchFromSagres() {
        Call call = createFirstGradesCall();
        LiveData<SagresResponse> gradesRsp = LiveDataCallAdapter.adapt(call);
        result.addSource(gradesRsp, response -> {
            result.removeSource(gradesRsp);
            //noinspection ConstantConditions
            if (response.isSuccessful()) {
                Document document = response.getDocument();
                result.postValue(Resource.loading(R.string.processing_information));
                executors.diskIO().execute(() -> {
                    saveResult(document);
                    executors.mainThread().execute(() -> result.setValue(Resource.loading(R.string.all_semester_part_completed)));
                    List<Call> calls = createCallsFromDocument(document);
                    //For each semester, create a call, this might block this thread
                    for (Call grdCall : calls) {
                        LiveData<SagresResponse> cRsp = LiveDataCallAdapter.adapt(grdCall);
                        result.addSource(cRsp, rsp -> {
                            result.removeSource(cRsp);
                            //noinspection ConstantConditions
                            if (rsp.isSuccessful()) {
                                Timber.d("Response for grades is successful");
                                Document doc = rsp.getDocument();
                                executors.diskIO().execute(() -> {
                                    saveResult(doc);
                                    executors.mainThread().execute(() -> result.setValue(Resource.loading(R.string.all_semester_part_completed)));
                                });
                            } else {
                                Timber.d("Response for grades is unsuccessful :(");
                                executors.mainThread().execute(() -> result.setValue(Resource.error("Failed", 500, R.string.failed_to_connect)));
                            }
                        });
                    }
                });
            } else {
                result.postValue(Resource.error(response.getMessage(), response.getCode(), R.string.failed_to_connect));
            }
        });
    }

    @NonNull
    protected abstract List<Call> createCallsFromDocument(@NonNull Document document);
    protected abstract void saveResult(@NonNull Document document);
    @NonNull
    protected abstract Call createFirstGradesCall();

    public LiveData<Resource<Integer>> asLiveData() {
        return result;
    }
}
