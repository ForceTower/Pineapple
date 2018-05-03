package com.forcetower.uefs.view.suggestion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.forcetower.uefs.R;
import com.forcetower.uefs.view.UBaseActivity;

import butterknife.BindView;
import dagger.android.AndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class SuggestionActivity extends UBaseActivity implements HasSupportFragmentInjector {
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static void startActivity(Context context, String message, String stackTrace) {
        Intent intent = new Intent(context, SuggestionActivity.class);
        intent.putExtra(SuggestionFragment.MESSAGE_CAUSE, message);
        intent.putExtra(SuggestionFragment.STACK_TRACE, stackTrace);
        context.startActivity(intent);
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SuggestionActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_suggestion, savedInstanceState);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.nav_title_feedback);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Fragment fragment;
        if (getIntent().hasExtra(SuggestionFragment.MESSAGE_CAUSE)) {
            fragment = SuggestionFragment.createFragment(
                    getIntent().getStringExtra(SuggestionFragment.MESSAGE_CAUSE),
                    getIntent().getStringExtra(SuggestionFragment.STACK_TRACE)
            );
        } else {
            fragment = SuggestionFragment.createFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return null;
    }
}
