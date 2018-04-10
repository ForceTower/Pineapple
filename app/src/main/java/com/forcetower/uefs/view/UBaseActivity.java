package com.forcetower.uefs.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.forcetower.uefs.GooglePlayGamesInstance;
import com.forcetower.uefs.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by João Paulo on 05/03/2018.
 * Base activity for all activities. Yay!
 */
public abstract class UBaseActivity extends AppCompatActivity implements AchievementsController {
    protected static final int PLAY_GAMES_SIGN_IN = 5001;
    protected static final int PLAY_GAMES_LEADERBOARD = 6000;

    @Inject
    public GooglePlayGamesInstance mPlayGamesInstance;

    protected SharedPreferences mPreferences;

    public void onCreate(@LayoutRes int layout, Bundle savedInstanceState) {
        themeSelector();
        super.onCreate(savedInstanceState);
        setContentView(layout);
        ButterKnife.bind(this);
        Timber.d("mPlayGames Is: " + mPlayGamesInstance);
        mPlayGamesInstance.createGoogleClient();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPreferences.getBoolean("google_play_games_enabled", false)) {
            signInSilently();
        }
    }

    protected void signIn() {
        startActivityForResult(mPlayGamesInstance.getGoogleSignInClient().getSignInIntent(), PLAY_GAMES_SIGN_IN);
    }

    protected void signInSilently() {
        mPlayGamesInstance.getGoogleSignInClient().silentSignIn().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Timber.d("Successfully connected to Google Play Games!");
                onGooglePlayGamesConnected(task.getResult());
            } else {
                Timber.d("Failed to connect to Google Play Games...");
            }
        }).addOnFailureListener(failure -> {
            Timber.d("Exception on connect to Google Play Games! %s", failure.getMessage());
            failure.printStackTrace();
        });
    }

    protected void onGooglePlayGamesConnected(GoogleSignInAccount result) {
        mPlayGamesInstance.onConnected(result);
        mPlayGamesInstance.getGamesClient().setViewForPopups(getWindow().getDecorView().findViewById(android.R.id.content));
        unlockAchievements(getString(R.string.achievement_journey_start), mPlayGamesInstance.getAchievementsClient());
    }

    protected void openPlayGamesAchievements() {
        if (!mPlayGamesInstance.isSignedIn()) {
            Timber.d("Not connected... Thus you can't open this");
            mPlayGamesInstance.disconnect();
            return;
        }
        Task<Intent> allAchievements = mPlayGamesInstance.getAchievementsClient().getAchievementsIntent();
        allAchievements.addOnCompleteListener(task -> {
            if (task.isSuccessful())
                startActivityForResult(task.getResult(), PLAY_GAMES_LEADERBOARD);
            else
                Timber.d("Unsuccessful open Achievements Task");
        });
    }

    //Theme selection
    private void themeSelector() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preferences.getString("unes_selected_theme", "default_theme");

        if (!theme.equalsIgnoreCase("default_theme")) {

            if (theme.equalsIgnoreCase("ellen_theme"))
                setTheme(R.style.AppThemeEllen1);

            else if (theme.equalsIgnoreCase("random_1_theme"))
                setTheme(R.style.AppThemeForce1);

            else if (theme.equalsIgnoreCase("random_2_theme"))
                setTheme(R.style.AppThemeForce2);

            else if (theme.equalsIgnoreCase("gray_1_theme"))
                setTheme(R.style.AppThemeGray1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLAY_GAMES_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                onGooglePlayGamesConnected(account);
            } catch (ApiException apiException) {
                String message = apiException.getMessage();
                if (message == null || message.isEmpty()) {
                    message = getString(R.string.play_sign_in_other_error);
                }

                mPlayGamesInstance.onDisconnected();

                new AlertDialog.Builder(this)
                        .setMessage(message)
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            }
        }
    }
}
