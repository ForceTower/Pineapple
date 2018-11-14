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

package com.forcetower.uefs.svc;

import android.app.Notification;
import android.app.PendingIntent;
import android.arch.lifecycle.LifecycleService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.rep.service.BigTrayRepository;
import com.forcetower.uefs.ru.RUData;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class BigTrayService extends LifecycleService {
    private final int NOTIFICATION_BIG_TRAY = 187745;
    public static final String START_SERVICE_ACTION = "com.forcetower.uefs.bigtray.START_FOREGROUND_SERVICE";
    public static final String STOP_SERVICE_ACTION = "com.forcetower.uefs.bigtray.STOP_FOREGROUND_SERVICE";

    @Inject
    BigTrayRepository repository;
    @Nullable
    private RUData trayData;
    private boolean running = false;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        String action = intent != null ? intent.getAction() : null;

        if (action == null) {
            startComponent();
        } else if (action.equals(START_SERVICE_ACTION)) {
            startComponent();
        } else if (action.equals(STOP_SERVICE_ACTION)) {
            stopComponent();
        }

        return START_STICKY;
    }

    private void stopComponent() {
        running = false;
        stopForeground(true);
        stopSelf();
    }

    private void startComponent() {
        if (!running) {
            running = true;
            startForeground(NOTIFICATION_BIG_TRAY, createNotification(null));

            repository.beginWith(7000).observe(this, it -> {
                if (it != null && !it.equals(trayData)) {
                    trayData = it;
                    startForeground(NOTIFICATION_BIG_TRAY, createNotification(it));
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        repository.setRequesting(false);
        running = false;
    }

    private Notification createNotification(@Nullable RUData data) {
        Intent intent = new Intent(this, BigTrayService.class);
        intent.setAction(STOP_SERVICE_ACTION);

        PendingIntent pending = PendingIntent.getService(this, 0, intent, 0);
        return NotificationCreator.showBigTrayNotification(this, data, pending);
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, BigTrayService.class);
        context.startService(intent);
    }
}
