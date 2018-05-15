package com.forcetower.uefs.view;

import androidx.annotation.NonNull;

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
}
