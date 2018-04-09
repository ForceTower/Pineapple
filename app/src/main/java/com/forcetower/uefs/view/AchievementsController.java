package com.forcetower.uefs.view;

import android.support.annotation.NonNull;

import com.google.android.gms.games.AchievementsClient;

/**
 * Created by João Paulo on 08/04/2018.
 */
public interface AchievementsController {
    default void unlockAchievements(@NonNull String achievement, @NonNull AchievementsClient achievementsClient) {
        achievementsClient.unlock(achievement);
    }
}
