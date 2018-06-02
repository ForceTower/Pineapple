package com.forcetower.uefs.view.universe;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.forcetower.uefs.R;
import com.forcetower.uefs.view.UBaseActivity;

import javax.inject.Inject;

import butterknife.BindView;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class UniverseActivity extends UBaseActivity implements HasSupportFragmentInjector, ActivityController {
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
    @Inject
    UniverseNavigationController navigation;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    private boolean toolbarHidden;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, UniverseActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_universe, savedInstanceState);

        setupViews();

        if (savedInstanceState == null)
            onActivityCreated();
        else
            onActivityResumed(savedInstanceState);
    }

    private void setupViews() {
        setSupportActionBar(toolbar);

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
