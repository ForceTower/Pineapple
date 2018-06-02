package com.forcetower.uefs.svc.firebase;

import android.content.Context;
import android.os.Build;

import com.forcetower.uefs.db.dao.AccessDao;
import com.forcetower.uefs.db.dao.ProfileDao;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Profile;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
    @Inject
    AccessDao access;
    @Inject
    ProfileDao profileDao;

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

        Access a = access.getAccessDirect();
        Profile p = profileDao.getProfileDirect();
        if (a == null) return;
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("firebase_tokens");
        reference.child(a.getUsername()).child("token").setValue(refreshedToken);
        reference.child(a.getUsername()).child("device").setValue(Build.MANUFACTURER + " " + Build.MODEL);
        reference.child(a.getUsername()).child("android").setValue(Build.VERSION.SDK_INT);
        reference.child(a.getUsername()).child("name").setValue(p.getName());
    }
}
