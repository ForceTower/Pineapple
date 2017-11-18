package com.forcetower.uefs.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.forcetower.uefs.R;
import com.forcetower.uefs.activity.base.UEFSBaseActivity;
import com.forcetower.uefs.fragments.ScheduleFragment;

public class ConnectedActivity extends UEFSBaseActivity {
    private TextView mTextMessage;
    private Menu menu;
    private int count;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, ConnectedActivity.class);
        Bundle bundle = ActivityOptionsCompat.makeCustomAnimation(context,
                android.R.anim.fade_in, android.R.anim.fade_out).toBundle();

        context.startActivity(intent, bundle);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.navigation_home) {
                mTextMessage.setText(R.string.title_schedule);
                switchToFragment(ScheduleFragment.class);
                return true;
            } else if (id == R.id.navigation_dashboard) {
                mTextMessage.setText(R.string.title_dashboard);
                return true;
            } else if (id == R.id.navigation_notifications) {
                mTextMessage.setText(R.string.title_notifications);
                return true;
            }
            return false;
        }
    };

    private void switchToFragment(Class clazz) {
        String tag = clazz.getName();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment == null) {
            try {
                fragment = (Fragment) clazz.newInstance();
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment, tag).commit();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, tag).commit();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connected);

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);

        menu = navigation.getMenu();
        count = menu.size();

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            ScheduleFragment fragment = ScheduleFragment.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }
}
