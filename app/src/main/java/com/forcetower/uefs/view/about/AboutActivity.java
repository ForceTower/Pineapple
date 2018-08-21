package com.forcetower.uefs.view.about;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.appcompat.widget.Toolbar;
import android.transition.Slide;
import android.view.MenuItem;
import android.view.View;

import com.forcetower.uefs.R;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;
import com.forcetower.uefs.view.about.fragments.AboutFragment;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class AboutActivity extends UBaseActivity implements HasSupportFragmentInjector {
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (VersionUtils.isLollipop()) toolbar.setElevation(10);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.about_app_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (savedInstanceState == null) {
            Fragment fragment = new AboutFragment();
            if (VersionUtils.isLollipop()) {
                fragment.setExitTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.START, getResources().getConfiguration().getLayoutDirection())));
                fragment.setEnterTransition(new Slide(GravityCompat.getAbsoluteGravity(GravityCompat.END, getResources().getConfiguration().getLayoutDirection())));
                fragment.setAllowEnterTransitionOverlap(false);
                fragment.setAllowReturnTransitionOverlap(false);
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void thirdParty(View view) {
        new LibsBuilder()
                .withActivityStyle(Libs.ActivityStyle.LIGHT)
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .start(this);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
