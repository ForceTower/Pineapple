package com.forcetower.uefs.activity.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.managers.SagresAccessManager;
import com.forcetower.uefs.sagres_sdk.managers.SagresProfileManager;

/**
 * Created by João Paulo on 18/11/2017.
 */

public abstract class UEFSBaseActivity extends AppCompatActivity {
    SagresPortalSDK.SagresSDKInitializationCallback initializationCallback = new SagresPortalSDK.SagresSDKInitializationCallback() {
        @Override
        public void onFinishInit() {
            //onFinishInitializing();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SagresPortalSDK.initializeSdk(this, initializationCallback);

        SagresAccessManager.getInstance().loadCurrentAccess();
        SagresProfileManager.getInstance().loadCurrentProfile();

        if (SagresAccess.getCurrentAccess() != null && SagresProfile.getCurrentProfile() == null) {
            Log.i(Constants.APP_TAG, "Attempt to load user profile");
            SagresPortalSDK.getExecutor().execute(new Runnable() {
                @Override
                public void run() {
                    SagresProfile.fetchProfileForCurrentAccess();
                }
            });
        }
    }

    protected void onFinishInitializing() {

    }
}
