package com.forcetower.uefs.game.g2048.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.forcetower.uefs.R;
import com.forcetower.uefs.game.g2048.tools.KeyListener;
import com.forcetower.uefs.util.VersionUtils;
import com.forcetower.uefs.view.UBaseActivity;

import dagger.android.AndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import timber.log.Timber;

public class Game2048Activity extends UBaseActivity implements HasSupportFragmentInjector {

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, Game2048Activity.class);
        activity.startActivity(intent);
        activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);

        Window window = getWindow();
        if (VersionUtils.isKitkat()) {
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new Game2048Fragment(), "Game Fragment")
                    .commit();
        }

        unlockAchievements(getString(R.string.achievement_you_found_me), mPlayGamesInstance);
        revealAchievement(getString(R.string.achievement_you_are_good_in_2048), mPlayGamesInstance);
        revealAchievement(getString(R.string.achievement_unes_2048_champion), mPlayGamesInstance);
        revealAchievement(getString(R.string.achievement_you_tried_2048), mPlayGamesInstance);
        revealAchievement(getString(R.string.achievement_practice_makes_perfect), mPlayGamesInstance);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Timber.d("Action: " + keyCode + " event: " + event);
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            Fragment current = getSupportFragmentManager().findFragmentByTag("Game Fragment");
            if (current != null && current instanceof KeyListener) {
                return ((KeyListener) current).onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return null;
    }

}
