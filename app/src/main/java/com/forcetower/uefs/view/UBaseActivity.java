package com.forcetower.uefs.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.forcetower.uefs.GooglePlayGamesInstance;
import com.forcetower.uefs.R;
import com.forcetower.uefs.util.NetworkUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;
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

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("mPlayGames Is: " + mPlayGamesInstance);
        mPlayGamesInstance.createGoogleClient();
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPreferences.getBoolean("google_play_games_enabled", false)) {
            signInSilently();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void signIn() {
        if (NetworkUtils.isNetworkAvailable(this))
            startActivityForResult(mPlayGamesInstance.getGoogleSignInClient().getSignInIntent(), PLAY_GAMES_SIGN_IN);
        else
            Toast.makeText(this, R.string.you_are_not_connected, Toast.LENGTH_SHORT).show();
    }

    public void signInSilently() {
        if (!NetworkUtils.isNetworkAvailable(this))
            return;

        mPlayGamesInstance.getGoogleSignInClient().silentSignIn().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Timber.d("Successfully connected to Google Play Games!");
                onGooglePlayGamesConnected(task.getResult());
            } else {
                Timber.d("Failed to connect to Google Play Games...");
                Exception e = task.getException();
                if (e == null) {
                    Timber.d("Failed and exception is null");
                } else {
                    e.printStackTrace();
                }
                signIn();
            }
        }).addOnFailureListener(failure -> {
            Timber.d("Exception on connect to Google Play Games! %s", failure.getMessage());
            failure.printStackTrace();
        });
    }

    protected void onGooglePlayGamesConnected(GoogleSignInAccount result) {
        mPlayGamesInstance.onConnected(result);
        mPlayGamesInstance.getGamesClient().setViewForPopups(getWindow().getDecorView().findViewById(android.R.id.content));
        unlockAchievements(getString(R.string.achievement_journey_start), mPlayGamesInstance);
        checkAchievements();
        if (mPlayGamesInstance.hasPlayerUnlockedSwitchAchievement()) {
            unlockAchievements(getString(R.string.achievement_now_i_understand_and_get_it), mPlayGamesInstance);
        }
    }

    public void checkAchievements() {}

    public void openPlayGamesAchievements() {
        if (!mPlayGamesInstance.isSignedIn() || !NetworkUtils.isNetworkAvailable(this) || mPlayGamesInstance.getAchievementsClient() == null) {
            Timber.d("Not connected... Thus you can't open this because of: %s %s %s", !mPlayGamesInstance.isSignedIn(), !NetworkUtils.isNetworkAvailable(this), mPlayGamesInstance.getAchievementsClient() == null);
            if (!NetworkUtils.isNetworkAvailable(this)) {
                Toast.makeText(this, R.string.connect_to_the_internet, Toast.LENGTH_SHORT).show();
            } else {
                mPlayGamesInstance.disconnect();
            }
            return;
        }
        Task<Intent> allAchievements = mPlayGamesInstance.getAchievementsClient().getAchievementsIntent();
        allAchievements.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                assert task.getResult() != null;
                startActivityForResult(task.getResult(), PLAY_GAMES_LEADERBOARD);
            } else {
                Timber.d("Unsuccessful open Achievements Task");
                Timber.d(task.getException());
                Toast.makeText(this, R.string.unable_to_open_achievements, Toast.LENGTH_SHORT).show();
            }
        });
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
                } else if (apiException.getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    message = getString(R.string.google_play_sign_in_cancelled);
                } else if (apiException.getStatusCode() == 4) {
                    message = getString(R.string.game_api_failed);
                } else if (apiException.getStatusCode() == 12500) {
                    message = getString(R.string.you_need_to_update_play_services);
                } else if (apiException.getStatusCode() == GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS){
                    message = getString(R.string.google_play_sign_in_currently_in_progress);
                } else if (apiException.getStatusCode() == GoogleSignInStatusCodes.NETWORK_ERROR){
                    message = getString(R.string.no_internet_connection);
                } else {
                    message = message.concat("\n")
                            .concat(GoogleSignInStatusCodes.getStatusCodeString(apiException.getStatusCode()))
                            .concat("\n")
                            .concat(getString(R.string.play_services_error));
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
