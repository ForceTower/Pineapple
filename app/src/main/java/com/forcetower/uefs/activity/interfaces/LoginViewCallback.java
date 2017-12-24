package com.forcetower.uefs.activity.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.managers.SagresProfileManager;

/**
 * Created by João Paulo on 18/11/2017.
 */

public interface LoginViewCallback {
    void onLoginClicked(String username, String password);
    void onLoginFailed(Throwable throwable);
    void onLoginSuccess();
}