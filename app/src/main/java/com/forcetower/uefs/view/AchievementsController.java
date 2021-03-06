package com.forcetower.uefs.view;

import android.support.annotation.NonNull;

import com.forcetower.uefs.GooglePlayGamesInstance;
import com.google.android.gms.games.AchievementsClient;

/**
 * Created by João Paulo on 08/04/2018.
 */
public interface AchievementsController {
    default void unlockAchievements(@NonNull String achievement, @NonNull GooglePlayGamesInstance playGamesInstance) {
        AchievementsClient achievementsClient = playGamesInstance.getAchievementsClient();
        if (achievementsClient != null && playGamesInstance.isSignedIn())
            achievementsClient.unlock(achievement);
    }

    default void publishAchievementProgress(String achievement, int value, GooglePlayGamesInstance playGamesInstance) {
        AchievementsClient achievementsClient = playGamesInstance.getAchievementsClient();
        if (achievementsClient != null && playGamesInstance.isSignedIn())
            achievementsClient.setSteps(achievement, value);
    }

    default void incrementAchievementProgress(String achievement, int value, GooglePlayGamesInstance playGamesInstance) {
        AchievementsClient achievementsClient = playGamesInstance.getAchievementsClient();
        if (achievementsClient != null && playGamesInstance.isSignedIn())
            achievementsClient.increment(achievement, 1);
    }

    default void revealAchievement(String achievement, GooglePlayGamesInstance playGamesInstance) {
        AchievementsClient achievementsClient = playGamesInstance.getAchievementsClient();
        if (achievementsClient != null && playGamesInstance.isSignedIn())
            achievementsClient.reveal(achievement);
    }
}
