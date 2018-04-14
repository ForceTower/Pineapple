package com.forcetower.uefs;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesClient;
import com.google.android.gms.games.LeaderboardsClient;

import timber.log.Timber;

/**
 * Created by João Paulo on 09/04/2018.
 */
public class GooglePlayGamesInstance extends ContextWrapper {
    protected String playerName = null;
    protected GoogleSignInClient mGoogleSignInClient;
    protected AchievementsClient mAchievementsClient;
    protected LeaderboardsClient mLeaderboardsClient;
    protected GamesClient mGamesClient;
    protected SharedPreferences preferences;
    private MutableLiveData<GameConnectionStatus> mStatus;
    private boolean playerUnlockedSwitchAchievement = false;

    public GooglePlayGamesInstance(Context base) {
        super(base);
        mStatus = new MutableLiveData<>();
        preferences = PreferenceManager.getDefaultSharedPreferences(base);
    }

    public void createGoogleClient() {
        if (mGoogleSignInClient == null) {
            Timber.d("Created Google Play Client");
            mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN).build());
        } else {
            Timber.d("Google Play Client already exists");
        }
    }

    public void onConnected(GoogleSignInAccount result) {
        Timber.d("Connected");
        mAchievementsClient = Games.getAchievementsClient(this, result);
        mLeaderboardsClient = Games.getLeaderboardsClient(this, result);
        mGamesClient        = Games.getGamesClient(this, result);
        mStatus.postValue(GameConnectionStatus.CONNECTED);
        preferences.edit().putBoolean("google_play_games_enabled", true).apply();
    }

    public void disconnect() {
        mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
            boolean successful = task.isSuccessful();
            Timber.d("signOut(): " + (successful ? "success" : "failed"));
            mStatus.postValue(GameConnectionStatus.DISCONNECTED);
            onDisconnected();
            preferences.edit().putBoolean("google_play_games_enabled", false).apply();
        });
    }

    public void onDisconnected() {
        Timber.d("Disconnected");
        mLeaderboardsClient = null;
        mAchievementsClient = null;
    }

    public boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    public GamesClient getGamesClient() {
        return mGamesClient;
    }

    public AchievementsClient getAchievementsClient() {
        return mAchievementsClient;
    }

    public LeaderboardsClient getLeaderboardsClient() {
        return mLeaderboardsClient;
    }

    public GoogleSignInClient getGoogleSignInClient() {
        return mGoogleSignInClient;
    }

    public void changePlayerName(String other) {
        boolean change = false;
        Timber.d("Old player name: %s", playerName);
        Timber.d("New player name: %s", other);

        if (other == null)
            playerName = null;
        else if (playerName == null)
            playerName = other;
        else if (!playerName.equalsIgnoreCase(other)) {
            change = true;
            playerName = other;
        }
        if (change) playerUnlockedSwitchAchievement = true;
    }

    public LiveData<GameConnectionStatus> getPlayGameStatus() {
        return mStatus;
    }

    public boolean hasPlayerUnlockedSwitchAchievement() {
        Timber.d("Status of completition: %s", playerUnlockedSwitchAchievement);
        return playerUnlockedSwitchAchievement;
    }
}
