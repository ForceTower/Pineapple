package com.forcetower.uefs.view.connected;

import com.forcetower.uefs.GooglePlayGamesInstance;

/**
 * Created by João Paulo on 14/04/2018.
 */
public interface GamesAccountController {
    GooglePlayGamesInstance getPlayGamesInstance();
    void openPlayGamesAchievements();
    void signIn();
}
