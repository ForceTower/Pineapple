package com.forcetower.uefs.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.MenuItem;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.base.UEFSBaseActivity;
import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.domain.SagresMessage;

public class MessageActivity extends UEFSBaseActivity {
    private static SagresMessage messageInst;

    public static void startActivity(Context context, SagresMessage message) {
        Intent intent = new Intent(context, MessageActivity.class);
        /*Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();*/
        messageInst = message;
        context.startActivity(intent/*, bundle*/);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        if(messageInst == null)
            finish();
/*
        if (Utils.supportsMaterialDesign()) {
           getWindow().setEnterTransition(new Explode());
           getWindow().setExitTransition(new Explode());
        }
*/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title_message);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView className    = findViewById(R.id.tv_class_name);
        TextView messageText  = findViewById(R.id.tv_message_text);
        TextView senderName   = findViewById(R.id.tv_sender_name);
        TextView receivedTime = findViewById(R.id.tv_received_time);

        className.setText(messageInst.getClassName());
        messageText.setText(messageInst.getMessage());
        senderName.setText(messageInst.getSender());
        receivedTime.setText(getString(R.string.sent_at, messageInst.getReceivedTime()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
