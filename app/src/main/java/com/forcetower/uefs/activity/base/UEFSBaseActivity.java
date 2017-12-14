package com.forcetower.uefs.activity.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;

/**
 * Created by João Paulo on 18/11/2017.
 */

public abstract class UEFSBaseActivity extends AppCompatActivity {
    SagresPortalSDK.SagresSDKInitializationCallback initializationCallback = new SagresPortalSDK.SagresSDKInitializationCallback() {
        @Override
        public void onFinishInit() {
            onFinishInitializing();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SagresPortalSDK.initializeSdk(this, initializationCallback);
    }

    protected void onFinishInitializing() {

    }
}
