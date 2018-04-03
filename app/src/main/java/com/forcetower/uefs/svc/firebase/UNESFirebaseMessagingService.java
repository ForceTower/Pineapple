package com.forcetower.uefs.svc.firebase;

import com.forcetower.uefs.ntf.NotificationCreator;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import dagger.android.AndroidInjection;
import timber.log.Timber;

/**
 * Created by João Paulo on 02/04/2018.
 */
public class UNESFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidInjection.inject(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Timber.d("Message Type: %s", remoteMessage.getMessageType());
        Timber.d("Message Data: %s", remoteMessage.getData());

        String title = null;
        String body = null;
        String icon = null;
        String sound = null;
        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            title = notification.getTitle();
            body = notification.getBody();
            icon = notification.getIcon();
            sound = notification.getTitle();
            NotificationCreator.createFirebaseSimpleNotification(getBaseContext(), notification);
        } else {
            Timber.d("Null notification");

        }

        Timber.d("Title: %s", title);
        Timber.d("Body: %s", body);
        Timber.d("Icon: %s", icon);
        Timber.d("Sound: %s", sound);
    }
}
