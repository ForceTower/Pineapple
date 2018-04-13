package com.forcetower.uefs.view;

import android.support.annotation.NonNull;

import com.forcetower.uefs.GooglePlayGamesInstance;
import com.google.android.gms.games.AchievementsClient;
import com.google.android.gms.games.Games;

/**
 * Created by João Paulo on 08/04/2018.
 */
public interface AchievementsController {
    default void unlockAchievements(@NonNull String achievement, @NonNull GooglePlayGamesInstance playGamesInstance) {
        AchievementsClient achievementsClient = playGamesInstance.getAchievementsClient();
        if (achievementsClient != null && playGamesInstance.isSignedIn())
            achievementsClient.unlock(achievement);
    }
}
