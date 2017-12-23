package com.forcetower.uefs.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.forcetower.uefs.R;

public class SuggestionActivity extends AppCompatActivity {
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, SuggestionActivity.class);
        context.startActivity(intent);
    }

    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);

        editText = findViewById(R.id.et_suggestion);

        findViewById(R.id.btn_submit).setOnClickListener(view -> {
            composeEmail(editText.getText().toString());
            finish();
        });
    }

    public void composeEmail(String body) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","joaopaulo761@gmail.com", null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "[UNES]App_Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)));
    }
}
