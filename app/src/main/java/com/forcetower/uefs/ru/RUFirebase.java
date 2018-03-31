package com.forcetower.uefs.ru;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;


public class RUFirebase {
    private final FirebaseDatabase firebaseDatabase;
    private final FirebaseApp firebaseApp;

    public RUFirebase(Context context) {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("813602503122")
                .setApiKey("AIzaSyAoIe4OjDCkEsASjiidXuGEk4vjQn7XySo")
                .setDatabaseUrl("https://uefs-9a104.firebaseio.com")
                .setProjectId("uefs-9a104")
                .setStorageBucket("uefs-9a104.appspot.com")
                .setGcmSenderId("813602503122")
                .build();

        firebaseApp = FirebaseApp.initializeApp(context, options, "BigTray");
        firebaseDatabase = FirebaseDatabase.getInstance(firebaseApp);
    }

    public FirebaseApp getFirebaseApp() {
        return firebaseApp;
    }

    public FirebaseDatabase getFirebaseDatabase() {
        return firebaseDatabase;
    }
}
