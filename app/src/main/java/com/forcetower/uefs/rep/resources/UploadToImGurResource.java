package com.forcetower.uefs.rep.resources;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import android.graphics.Bitmap;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.db_service.helper.ImGurDataObject;
import com.forcetower.uefs.db_service.helper.ImGurUploadResponse;
import com.forcetower.uefs.rep.helper.Resource;
import com.forcetower.uefs.util.ImageUtils;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;
import timber.log.Timber;

/**
 * Created by João Paulo on 16/06/2018.
 */
public abstract class UploadToImGurResource {
    private final MediatorLiveData<Resource<ImGurDataObject>> result;
    private final AppExecutors executors;
    private final Bitmap bitmap;

    @MainThread
    public UploadToImGurResource(@NonNull AppExecutors executors, @NonNull Bitmap bitmap) {
        this.executors = executors;
        this.bitmap = bitmap;
        result = new MediatorLiveData<>();
        result.postValue(Resource.loading(null));

        uploadSteps();
    }

    private void uploadSteps() {
        LiveData<String> base64Src = ImageUtils.encodeImage(bitmap, executors);
        result.addSource(base64Src, base64 -> {
            result.removeSource(base64Src);
            Call call = createCall(base64);
            executors.networkIO().execute(() -> {
                try {
                    Response response = call.execute();
                    if (response.isSuccessful()) {
                        ImGurUploadResponse imObject = processResponse(response.body().string());
                        result.postValue(Resource.success(imObject.getData()));
                    } else {
                        result.postValue(Resource.error(response.message(), response.code(), new Exception("Unsuccessful Response")));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Timber.d("Request failed");
                    result.postValue(Resource.error("Request Timeout", 500, e));
                }
            });
        });
    }

    private ImGurUploadResponse processResponse(String string) {
        Gson gson = new Gson();
        return gson.fromJson(string, ImGurUploadResponse.class);
    }

    protected abstract Call createCall(String base64);

    public LiveData<Resource<ImGurDataObject>> asLiveData() {
        return result;
    }
}
