package com.forcetower.uefs.view.event;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Window;
import android.view.animation.AnimationUtils;

import com.forcetower.uefs.R;
import com.forcetower.uefs.databinding.ActivityEventDetailsBinding;
import com.forcetower.uefs.db_service.entity.Event;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.vm.UEFSViewModelFactory;
import com.forcetower.uefs.vm.service.EventsViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

public class EventDetailsActivity extends AppCompatActivity implements HasSupportFragmentInjector {
    private ActivityEventDetailsBinding binding;
    private String uuid;

    @Inject
    UEFSViewModelFactory viewModelFactory;

    public static Intent startActivity(Context context, String uuid) {
        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra("uuid", uuid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_event_details);
        uuid = getIntent().getStringExtra("uuid");

        if (savedInstanceState == null)
            setupTransitions();

        setupToolbar();
        setupViewModel();
    }

    private void setupTransitions() {
        if (VersionUtils.isLollipop()) {
            Slide slide = new Slide(Gravity.BOTTOM);
            slide.setInterpolator(
                    AnimationUtils.loadInterpolator(
                            this,
                            android.R.interpolator.linear_out_slow_in
                    )
            );
            getWindow().setEnterTransition(slide);
        }
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowHomeEnabled(true);
        }
    }

    private void setupViewModel() {
        EventsViewModel viewModel = ViewModelProviders.of(this, viewModelFactory).get(EventsViewModel.class);
        viewModel.getEvent(uuid).observe(this, this::onReceiveEvent);
    }

    private void onReceiveEvent(Event event) {
        binding.setEvent(event);
        binding.executePendingBindings();
        loadImage(event.getImageUrl());
        binding.collapsingToolbar.setTitle(event.getName());
        binding.collapsingToolbar.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
    }

    private void loadImage(String url) {
        Picasso.with(this).load(url)
                .placeholder(R.drawable.ic_unes_large_image_512)
                .into(binding.eventImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        Drawable drawable = binding.eventImage.getDrawable();
                        if (drawable instanceof BitmapDrawable) {
                            Palette palette = Palette.from(((BitmapDrawable) drawable).getBitmap())
                                    .generate();

                            int vibrant  = palette.getVibrantColor(getResources().getColor(R.color.colorPrimary));
                            int light    = palette.getLightVibrantColor(getResources().getColor(R.color.white));
                            int dominant = palette.getDominantColor(getResources().getColor(R.color.white));

                            Timber.d("Light: " + light + " Vibrant: " + vibrant);
                            
                            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                                binding.collapsingToolbar.setContentScrimColor(vibrant);
                                binding.collapsingToolbar.setCollapsedTitleTextColor(dominant);
                                binding.collapsingToolbar.setStatusBarScrimColor(vibrant);

                                if (VersionUtils.isLollipop()) {
                                    Window window = getWindow();
                                    window.setNavigationBarColor(vibrant);
                                    window.setStatusBarColor(vibrant);
                                }
                            } else {
                                Timber.d("Not in a valid state");
                            }
                        } else {
                            Timber.e("Drawable is not an instance of BitmapDrawable");
                        }
                    }

                    @Override
                    public void onError() {
                        binding.collapsingToolbar.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
                        Timber.d("Failed loading image");
                    }
                });
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return null;
    }
}
