package com.forcetower.uefs.svc.firebase;

import android.content.Context;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import timber.log.Timber;

/**
 * Created by João Paulo on 02/04/2018.
 */
public class UNESFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Inject
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Timber.d("Firebase Token: %s", refreshedToken);
        Timber.d("Is context null? %s", context);
    }
}
