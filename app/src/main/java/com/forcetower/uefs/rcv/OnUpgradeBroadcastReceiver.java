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

package com.forcetower.uefs.rcv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.forcetower.uefs.AppExecutors;
import com.forcetower.uefs.BuildConfig;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.ntf.NotificationCreator;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class OnUpgradeBroadcastReceiver extends BroadcastReceiver {
    @Inject
    AppExecutors executors;
    @Inject
    AppDatabase database;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction()))
            return;

        AndroidInjection.inject(this, context);
        siecomp(context);
    }

    private void siecomp(Context context) {
        executors.diskIO().execute(() -> {
            Profile profile = database.profileDao().getProfileDirect();
            int reference = 0;
            if (profile != null) reference = profile.getCourseReference();
            if (reference < 2) {
                NotificationCreator.createSiecompNotification(context);
            }
        });
    }
}
