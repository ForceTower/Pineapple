package com.forcetower.uefs.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.forcetower.uefs.R;

public class LoginActivity extends AppCompatActivity {
    private Button btn_login;
    private EditText et_username;
    private EditText et_password;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = findViewById(R.id.btn_login);
        et_username = findViewById(R.id.username_form);
        et_password = findViewById(R.id.password_form);
        progressBar = findViewById(R.id.login_progress);
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

            connectToPortal(username, password);
        }
    }

    private void connectToPortal(String username, String password) {
        
    }
}
