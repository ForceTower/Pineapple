package com.forcetower.uefs.view.universe;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ActivityUniverseBinding;
import com.forcetower.uefs.view.UBaseActivity;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class UniverseActivity extends UBaseActivity implements HasSupportFragmentInjector, ActivityController {
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    UniverseNavigationController navigation;

    private boolean toolbarHidden;
    private ActivityUniverseBinding binding;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UniverseActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_universe);
        setupViews();

        if (savedInstanceState == null)
            onActivityCreated();
        else
            onActivityResumed(savedInstanceState);
    }

    private void setupViews() {
        setSupportActionBar(binding.toolbar);

        if (getSupportActionBar() != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(248, 248,248)));
        }
    }

    private void onActivityCreated() {
        navigation.initiateNavigation();
    }

    private void onActivityResumed(@NonNull Bundle savedInstanceState) {
        toolbarHidden = savedInstanceState.getBoolean("toolbar_hidden", false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("toolbar_hidden", toolbarHidden);
    }

    public void hideToolbar() {
        if (toolbarHidden) return;

        toolbarHidden = true;
        if (getSupportActionBar() != null) getSupportActionBar().hide();
    }

    public void showToolbar() {
        if (!toolbarHidden) return;

        toolbarHidden = false;
        if (getSupportActionBar() != null) getSupportActionBar().show();
    }

    public boolean isToolbarHidden() {
        return toolbarHidden;
    }

    public void setToolbarTitle(String title) {
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(title);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
