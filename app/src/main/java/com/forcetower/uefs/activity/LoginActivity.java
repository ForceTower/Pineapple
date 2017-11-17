package com.forcetower.uefs.activity;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.forcetower.uefs.Constants;
import com.forcetower.uefs.R;
import com.forcetower.uefs.UEFSApplication;
import com.forcetower.uefs.helpers.JavaNetCookieJar;
import com.forcetower.uefs.helpers.PrefUtils;
import com.forcetower.uefs.html_parser.SagresParser;
import com.forcetower.uefs.model.UClass;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
    private Button btn_login;
    private EditText et_username;
    private EditText et_password;
    private ProgressBar progressBar;

    private Call currentCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        et_username = findViewById(R.id.username_form);
        et_password = findViewById(R.id.password_form);
        progressBar = findViewById(R.id.login_progress);

        boolean connected = PrefUtils.get(this, "connected", false);
        String html = PrefUtils.get(this, "html", "");
        //String savedClasses = PrefUtils.get(this, "classes", "");

        if (connected) {
            /*if (!savedClasses.trim().equals("")) {
                HashMap classes = new Gson().fromJson(savedClasses, HashMap.class);
                ((UEFSApplication)getApplication()).saveClasses(classes);
                ConnectedActivity.startActivity(this);
                finish();
            }*/

            if (!html.trim().equals("")) {
                ParsingActivity.startActivity(this, html, false);
                finish();
            }
        }
    }

    public void login(View view) {
        String username = et_username.getText().toString();
        String password = et_password.getText().toString();

        if (username.length() < 4)
            et_username.setError(getString(R.string.invalid_username));
        else if (password.length() < 4)
            et_password.setError(getString(R.string.invalid_password));
        else {
            btn_login.setClickable(false);
            btn_login.setAlpha(0.4f);
            btn_login.setText(R.string.connecting);
            progressBar.setVisibility(View.VISIBLE);
            et_username.setEnabled(false);
            et_password.setEnabled(false);

            connectToPortal(username, password);
        }
    }

    private void connectToPortal(final String username, final String password) {
        RequestBody requestBody = new FormBody.Builder()
                .add("__EVENTTARGET", "")
                .add("__EVENTARGUMENT", "")
                .add("__VIEWSTATE", "/wEPDwUKMTc5MDkxMTc2NA9kFgJmD2QWBAIBD2QWDAIEDxYCHgRocmVmBT1+L0FwcF9UaGVtZXMvTmV3VGhlbWUvQWNlc3NvRXh0ZXJuby5jc3M/ZnA9NjM2Mzk4MDY3NDQwMDAwMDAwZAIFDxYCHwAFOH4vQXBwX1RoZW1lcy9OZXdUaGVtZS9Db250ZXVkby5jc3M/ZnA9NjM2Mzk4MDY3NDQwMDAwMDAwZAIGDxYCHwAFOX4vQXBwX1RoZW1lcy9OZXdUaGVtZS9Fc3RydXR1cmEuY3NzP2ZwPTYzNjIxNDcxMjMwMDAwMDAwMGQCBw8WAh8ABTl+L0FwcF9UaGVtZXMvTmV3VGhlbWUvTWVuc2FnZW5zLmNzcz9mcD02MzYyMTQ3MTIzMDAwMDAwMDBkAggPFgIfAAU2fi9BcHBfVGhlbWVzL05ld1RoZW1lL1BvcFVwcy5jc3M/ZnA9NjM2MjE0NzEyMzAwMDAwMDAwZAIJDxYCHwAFWC9Qb3J0YWwvUmVzb3VyY2VzL1N0eWxlcy9BcHBfVGhlbWVzL05ld1RoZW1lL05ld1RoZW1lMDEvZXN0aWxvLmNzcz9mcD02MzYxMDU4MjY2NDAwMDAwMDBkAgMPZBYEAgcPDxYEHgRUZXh0BQ1TYWdyZXMgUG9ydGFsHgdWaXNpYmxlaGRkAgsPZBYGAgEPDxYCHwJoZGQCAw88KwAKAQAPFgIeDVJlbWVtYmVyTWVTZXRoZGQCBQ9kFgICAg9kFgICAQ8WAh4LXyFJdGVtQ291bnRmZGTS+Y3bntF2UZMwIIXP8cpv13rKAw==")
                .add("__VIEWSTATEGENERATOR", "BB137B96")
                .add("__EVENTVALIDATION", "/wEdAATbze7D9s63/L1c2atT93YlM4nqN81slLG8uEFL8sVLUjoauXZ8QTl2nEJmPx53FYhjUq3W1Gjeb7bKHHg4dlob4GWO7EiBlTRJt8Yw8hywpn30EZA=")
                .add("ctl00$PageContent$LoginPanel$UserName", username)
                .add("ctl00$PageContent$LoginPanel$Password", password)
                .add("ctl00$PageContent$LoginPanel$LoginButton", "Entrar")
                .build();

        final Request request = new Request.Builder()
                .url(Constants.SAGRES_LOGIN)
                .post(requestBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();

        CookieHandler cookieHandler = new CookieManager();

        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(true)
                .cookieJar(new JavaNetCookieJar(cookieHandler))
                .build();

        Call call = client.newCall(request);
        currentCall = call;

        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull final IOException e) {
                Log.d(Constants.APP_TAG, "Problem in the paradise... Call failure");
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        returnUiToDefault(false);
                        Toast.makeText(LoginActivity.this, R.string.login_failed_response, Toast.LENGTH_SHORT).show();
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String html = response.body().string();
                    html = SagresParser.changeCharset(html);

                    boolean connected = SagresParser.connected(html);
                    if (!connected) {
                        returnUiToDefault(true);
                    } else {
                        ((UEFSApplication)getApplication()).saveHtml(html);
                        PrefUtils.save(LoginActivity.this, "html", html);
                        PrefUtils.save(LoginActivity.this, "connected", true);
                        //TODO: Save password on device?? (Yes, for now, i'm to lazy for typing it every time)
                        PrefUtils.save(LoginActivity.this, "username", username);
                        PrefUtils.save(LoginActivity.this, "password", password);

                        returnUiToDefault(false);
                        ParsingActivity.startActivity(LoginActivity.this, html, true);
                        finish();
                    }

                } else {
                    Log.d(Constants.APP_TAG, "Problem in the paradise... Unsuccessful response");
                    returnUiToDefault(false);
                }

                currentCall = null;
            }
        });
    }

    private void returnUiToDefault(final boolean error) {
        currentCall = null;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btn_login.setClickable(true);
                btn_login.setAlpha(1f);
                btn_login.setText(R.string.login_btn);
                progressBar.setVisibility(View.INVISIBLE);
                et_username.setEnabled(true);
                et_password.setEnabled(true);

                if (error)
                    et_password.setError(getString(R.string.incorrect_credentials));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (currentCall != null && !currentCall.isExecuted()) {
            currentCall.cancel();

            returnUiToDefault(false);
        } else {
            super.onBackPressed();
        }
    }
}
