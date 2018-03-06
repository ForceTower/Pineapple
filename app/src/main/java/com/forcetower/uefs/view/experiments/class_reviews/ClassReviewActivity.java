package com.forcetower.uefs.view.experiments.class_reviews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.forcetower.uefs.R;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.content.UNEService;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;
import com.forcetower.uefs.view.UEFSBaseActivity;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClassReviewActivity extends UEFSBaseActivity {
    private static final String TAG = "ClassReviewActivity";
    @BindView(R.id.loading_content)
    ViewGroup loadingContent;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Inject
    UNEService webService;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ClassReviewActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_review);
        ButterKnife.bind(this);
        ((UEFSApplication)getApplication()).getApplicationComponent().inject(this);


        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.class_review_discover_title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        performWebCalls();
    }

    private void performWebCalls() {
        Call<Void> send = webService.sendInformation(SagresProfile.getCurrentProfile());
        Call<Void> recv = webService.getInformation();

        send.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                Log.d(TAG, "onResponse: received response\n");
                receiveTime();
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: send failed. " + t.getMessage());
                receiveTime();
            }

            private void receiveTime() {
                recv.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {

                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {

                    }
                });
            }
        });
    }

    @MainThread
    public void setLoadingContent(boolean value) {
        if (value) Utils.fadeIn(loadingContent, this);
        else Utils.fadeOut(loadingContent, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
