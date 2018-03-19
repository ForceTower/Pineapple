package com.forcetower.uefs.util.network;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.forcetower.uefs.sgrs.SagresResponse;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LiveDataCallAdapter  {

    public static LiveData<SagresResponse> adapt(Call call) {
        return new LiveData<SagresResponse>() {
            AtomicBoolean started = new AtomicBoolean(false);
            @Override
            protected void onActive() {
                super.onActive();
                if (started.compareAndSet(false, true)) {
                    call.enqueue(new Callback() {
                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) {
                            postValue(new SagresResponse(response));
                        }

                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException throwable) {
                            postValue(new SagresResponse(throwable));
                        }
                    });
                }
            }
        };
    }
}