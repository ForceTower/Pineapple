package com.forcetower.uefs.view;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.sagres_sdk.managers.SagresProfileManager;

/**
 * Created by João Paulo on 18/11/2017.
 */

public abstract class UEFSBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SagresPortalSDK.initializeSdk(this, this::onFinishInitializing);
    }

    protected void onFinishInitializing() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        SagresProfileManager.getInstance().loadCurrentProfile();
    }

    public void replaceFragmentContainer(FragmentManager manager, Fragment fragment, int id, String tag) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(id, fragment, tag);
        transaction.commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
