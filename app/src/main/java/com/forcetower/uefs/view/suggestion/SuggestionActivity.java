package com.forcetower.uefs.view.suggestion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.forcetower.uefs.R;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;

import butterknife.BindView;
import dagger.android.AndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class SuggestionActivity extends UBaseActivity implements HasSupportFragmentInjector {
    private static final String MESSAGE_CAUSE = "exception_message";
    private static final String STACK_TRACE = "stack_trace";
    @BindView(R.id.et_suggestion)
    EditText editText;
    @BindView(R.id.btn_submit)
    Button btnSubmit;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static void startActivity(Context context, String message, StackTraceElement[] stackTrace) {
        Intent intent = new Intent(context, SuggestionActivity.class);
        intent.putExtra(MESSAGE_CAUSE, message);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Stack Trace START\n");
        for (StackTraceElement trace : stackTrace) {
            stringBuilder.append("\tat ").append(trace).append("\n");
        }
        stringBuilder.append("Stack Trace END");
        intent.putExtra(STACK_TRACE, stringBuilder.toString());

        context.startActivity(intent);
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SuggestionActivity.class);
        context.startActivity(intent);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(R.layout.activity_suggestion, savedInstanceState);

        if (getIntent().hasExtra(STACK_TRACE)) {
            String message = getIntent().getStringExtra(MESSAGE_CAUSE);
            String stack   = getIntent().getStringExtra(STACK_TRACE);
            String finalMessage = "- Não altere abaixo -\nCause: " + message + "\n\n" + stack;
            editText.setText(finalMessage);
            editText.setEnabled(false);
        }


        btnSubmit.setOnClickListener(view -> {
            String body = editText.getText().toString();
            if (body.trim().isEmpty()) {
                Toast.makeText(this, R.string.empty_suggestion_field, Toast.LENGTH_SHORT).show();
            } else {
                composeEmail(body);
                finish();
            }
        });

        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (VersionUtils.isLollipop()) toolbar.setElevation(10);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.suggestions);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    public void composeEmail(String body) {
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            int code = pInfo.versionCode;
            body = body.concat("\n\nVersion Name: " + version);
            body = body.concat("\nVersion Code: " + code);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","joaopaulo761@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[UNES]App_Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
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
