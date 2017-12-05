package com.forcetower.uefs.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.base.UEFSBaseActivity;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresProfile;

public class ParsingActivity extends UEFSBaseActivity {
    private TextView tv_information;
    private TextView tv_user_name;
    private Handler handler;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ParsingActivity.class);

        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

        context.startActivity(intent, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing);
        handler = new Handler(getMainLooper());

        tv_information = findViewById(R.id.tv_information);
        tv_information.setText(R.string.processing_info);
        tv_user_name = findViewById(R.id.tv_user_name);

        Utils.fadeIn(tv_information, this);

        SagresPortalSDK.addLoginListener(new SagresPortalSDK.LoginListener() {
            @Override
            public void alert(int code, String string) {
                if (code == 10 && !isFinishing()) {
                    finish();
                    return;
                }
                onReceiveUpdate(code, string);
            }
        });

        //May be unnecessary
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (SagresProfile.getCurrentProfile() != null) {
                    finish();
                }
            }
        }, 1500);
    }

    private void onReceiveUpdate(final int code, final String string) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (code == 1) {
                    tv_user_name.setText(getString(R.string.welcome, string));
                    Utils.fadeIn(tv_user_name, ParsingActivity.this);
                } else if (code == 2) {
                    tv_information.setText(R.string.prepare_to_fetch_grades);
                } else if (code == 3) {
                    String semester = string;
                    if (semester != null) {
                        semester = semester.substring(0, 4) + "." + semester.substring(4);
                    }
                    tv_information.setText(getString(R.string.fetching_grades_for_semester, semester));
                }
            }
        });
    }

    @Override
    protected void onFinishInitializing() {
        super.onFinishInitializing();
        if (SagresProfile.getCurrentProfile() != null) {
            finish();
        }
    }
}
