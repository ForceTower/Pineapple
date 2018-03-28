package com.forcetower.uefs.view.experimental.good_barrel;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.forcetower.uefs.R;
import com.forcetower.uefs.db.entity.Access;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.experimental.good_barrel.fragments.BarrelFragment;
import com.forcetower.uefs.vm.LoginViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

public class GoodBarrelActivity extends UBaseActivity implements HasSupportFragmentInjector {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @IdRes
    private int container;
    private FragmentManager fragmentManager;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, GoodBarrelActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_good_barrel, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        fragmentManager = getSupportFragmentManager();
        container = R.id.container;

        LoginViewModel loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
        loginViewModel.getAccess().observe(this, this::onAccessReceived);
    }

    private void onAccessReceived(Access access) {
        if (access == null) return;
        firebaseAuth.createUserWithEmailAndPassword(access.getUsername() + "@unes.uefs.br", "unes_app_password").addOnCompleteListener(create -> {
            if (create.isSuccessful()) {
                Timber.d("Connected = created");
                completedFirebaseSignIn();
            } else {
                firebaseAuth.signInWithEmailAndPassword(access.getUsername() + "@unes.uefs.br", "unes_app_password").addOnCompleteListener(login -> {
                    if (login.isSuccessful()) {
                        Timber.d("Connected = login");
                        completedFirebaseSignIn();
                    } else {
                        Timber.d("Failed login");
                    }
                });
            }
        });
    }

    private void completedFirebaseSignIn() {
        firebaseUser = firebaseAuth.getCurrentUser();

        navigateToBarrelMain();
    }

    private void navigateToBarrelMain() {
        fragmentManager.beginTransaction()
                .replace(container, new BarrelFragment())
                .commitAllowingStateLoss();
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
