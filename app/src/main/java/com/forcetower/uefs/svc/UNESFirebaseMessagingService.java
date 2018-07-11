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
            insertMessage(remoteMessage.getData());
        } else {
            Timber.d("Null notification");
            Timber.d("This is a data notification");

            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            String text = data.get("message");
            String image = data.get("image");
            NotificationCreator.createEventNotification(getBaseContext(), title, text, image);
        }
    }

    private void insertMessage(Map<String, String> data) {
        if (data == null) return;

        String notificationExtra = data.get("notification_unes_extra");
        if (notificationExtra == null || !notificationExtra.equalsIgnoreCase("save_message"))
            return;

        String message = data.get("notification_message");
        if (message == null) return;

        String author = data.get("notification_author");
        if (author == null) author = "ForceTower";

        String receivedClass = data.get("notification_class");
        if (receivedClass == null) receivedClass = "Notificações Gerais";

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int mon = calendar.get(Calendar.MONTH) + 1;
        int yea = calendar.get(Calendar.YEAR);
        String received = day + "/" + mon + "/" + yea;
        Message msg = new Message(author, message, received, receivedClass);
        msg.setNotified(1);

        new InsertMessageTask(database).doInBackground(msg);
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

    private static class InsertMessageTask extends AsyncTask<Message, Void, Void> {
        private AppDatabase database;
        private InsertMessageTask(AppDatabase database) {
            this.database = database;
        }
        @Override
        protected Void doInBackground(Message... messages) {
            database.messageDao().insertMessages(messages);
            return null;
        }
    }
}
