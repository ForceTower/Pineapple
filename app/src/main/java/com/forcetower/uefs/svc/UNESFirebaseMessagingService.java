package com.forcetower.uefs.svc;

import android.os.AsyncTask;
import android.os.Build;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.db.AppDatabase;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.db.entity.Message;
import com.forcetower.uefs.db.entity.Profile;
import com.forcetower.uefs.ntf.NotificationCreator;
import com.forcetower.uefs.service.ActionResult;
import com.forcetower.uefs.service.UNEService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Calendar;
import java.util.Map;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

/**
 * Created by João Paulo on 02/04/2018.
 */
public class UNESFirebaseMessagingService extends FirebaseMessagingService {
    @Inject
    AppDatabase database;
    @Inject
    UNEService service;

    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Timber.d("Message Type: %s", remoteMessage.getMessageType());
        Timber.d("Message Data: %s", remoteMessage.getData());

        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            NotificationCreator.createFirebaseSimpleNotification(getBaseContext(), notification);
        } else {
            Timber.d("Null notification");
            Timber.d("This is a data notification");

            Map<String, String> data = remoteMessage.getData();
            if (data != null) {
                if (data.containsKey("type")) {
                    switch (data.get("type")) {
                        case "event_notification":
                            eventCreation(data);
                            break;
                        case "service_notification":
                            serviceNotification(data);
                            break;
                        default:
                            Timber.d("Defaulted, nothing to do");
                            break;
                    }
                } else {
                    Timber.d("This is an invalid notification... Ignoring");
                }
            }
        }
    }

    private void serviceNotification(Map<String,String> data) {
        String title = data.get("title");
        String text = data.get("message");
        String image = data.get("image");
        NotificationCreator.createServiceNotification(getBaseContext(), title, text, image);
    }

    private void eventCreation(Map<String,String> data) {
        String title = data.get("title");
        String text = data.get("message");
        String image = data.get("image");
        String uuid = data.get("uuid");
        NotificationCreator.createEventNotification(getBaseContext(), title, text, image, uuid);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Timber.d("Firebase Token: " + token);
        try {
            Access a = database.accessDao().getAccessDirect();
            Profile p = database.profileDao().getProfileDirect();
            if (a == null) return;
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("firebase_tokens");
            reference.child(a.getUsernameFixed()).child("token").setValue(token);
            reference.child(a.getUsernameFixed()).child("device").setValue(Build.MANUFACTURER + " " + Build.MODEL);
            reference.child(a.getUsernameFixed()).child("android").setValue(Build.VERSION.SDK_INT);
            reference.child(a.getUsernameFixed()).child("name").setValue(p.getName());

            Call<ActionResult<Object>> call = service.postFirebaseToken(a.getUsername(), token);
            Response response = call.execute();
            if (response.isSuccessful()) {
                Timber.d("User Token was set");
            } else {
                Timber.d("Failed to set token");
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }

    }
}
