/*
 * Copyright (c) 2018.
 * João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This file is part of the UNES Open Source Project.
 *
 * UNES is licensed under the MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.forcetower.uefs.rep.service;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.AnyThread;
import android.support.annotation.MainThread;
import android.support.annotation.WorkerThread;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.ru.RUData;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

@Singleton
public class BigTrayRepository {
    private final OkHttpClient client;
    private final AppExecutors executors;
    private boolean requesting;

    private MediatorLiveData<RUData> data;

    @Inject
    public BigTrayRepository(OkHttpClient client, AppExecutors executors) {
        this.client = client;
        this.executors = executors;
    }

    @MainThread
    public LiveData<RUData> getData() {
        beginRequests();
        return data;
    }

    @MainThread
    private void beginRequests() {
        requesting = true;
        data = new MediatorLiveData<>();
        loop();
    }

    @MainThread
    public void setRequesting(boolean value) {
        this.requesting = value;
    }

    @MainThread
    private void loop() {
        Handler handler = new Handler(Looper.getMainLooper());
        Timber.d("Loop");
        if (requesting) {
            executors.networkIO().execute(() -> {
                RUData value = performRequest();
                data.postValue(value);
                handler.postDelayed(this::loop, 3500);
            });
        }
    }

    @WorkerThread
    private RUData performRequest() {
        Request request = createRequest();
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                String value = response.body().string();
                RUData ru = new RUData();
                ru.setCurrentTime(System.currentTimeMillis());
                if (value.equalsIgnoreCase("false")) {
                    ru.setAberto(false);
                    ru.setCotas("-1");
                } else {
                    String[] values = value.split(";");
                    if (values.length == 2) {
                        ru.setAberto(true);
                        ru.setMealType(values[0]);
                        ru.setCotas(values[1]);
                    } else {
                        ru.setError(true);
                        Crashlytics.log("They have changed the return...");
                    }
                }
                return ru;
            } else {
                Crashlytics.log("It seems that UEFS is trolling. Code was: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        RUData ru = new RUData();
        ru.setCurrentTime(System.currentTimeMillis());
        ru.setError(true);
        return ru;
    }

    @AnyThread
    private Request createRequest() {
        return new Request.Builder()
                .url("http://www.propaae.uefs.br/ru/getCotas.php")
                .get()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.92 Safari/537.36")
                .build();
    }
}
