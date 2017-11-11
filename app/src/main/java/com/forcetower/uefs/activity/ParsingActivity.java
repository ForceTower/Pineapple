package com.forcetower.uefs.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.html_parser.SagresParser;
import com.forcetower.uefs.model.UClass;

import java.util.HashMap;

public class ParsingActivity extends AppCompatActivity {
    private TextView tv_information;
    private boolean delayed;

    public static void startActivity(Context context, String html, boolean delayed) {
        Intent intent = new Intent(context, ParsingActivity.class);
        intent.putExtra("html", html);
        intent.putExtra("delayed", delayed);

        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

        context.startActivity(intent, bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parsing);

        tv_information = findViewById(R.id.tv_information);
        tv_information.setText(R.string.processing_info);
        Utils.fadeIn(tv_information, this);

        delayed = getIntent().getBooleanExtra("delayed", false);
        startLoadHtml(getIntent().getStringExtra("html"));
    }

    private void startLoadHtml(final String html) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (delayed) Thread.sleep(500);
                    fadeOut(tv_information);

                    String name = SagresParser.getUserName(html);

                    if (delayed) Thread.sleep(200);
                    updateInformation(getString(R.string.welcome, name));
                    fadeIn(tv_information);

                    if (delayed) Thread.sleep(1000);
                    fadeOut(tv_information);
                    if (delayed) Thread.sleep(100);
                    updateInformation(getString(R.string.finding_class_structure));
                    fadeIn(tv_information);

                    SagresParser.findSchedule(html);
                    //Thread.sleep(600);

                    //fadeOut(tv_information);
                    //Thread.sleep(100);
                    updateInformation(getString(R.string.finding_class_details));
                    //fadeIn(tv_information);

                    HashMap<String, UClass> classHashMap = SagresParser.findDetails(html);
                    ((UEFSApplication)getApplication()).saveClasses(classHashMap);

                    //Thread.sleep(500);
                    //fadeOut(tv_information);
                    //Thread.sleep(100);
                    updateInformation(getString(R.string.completed));
                    //fadeIn(tv_information);

                    //Thread.sleep(1000);
                    
                    completeActivity();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void completeActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConnectedActivity.startActivity(ParsingActivity.this);
                finish();
            }
        });
    }

    private void fadeOut(final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.fadeOut(view, ParsingActivity.this);
            }
        });
    }

    private void fadeIn(final View view) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Utils.fadeIn(view, ParsingActivity.this);
            }
        });
    }

    private void updateInformation(final String information) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_information.setText(information);
            }
        });
    }
}
