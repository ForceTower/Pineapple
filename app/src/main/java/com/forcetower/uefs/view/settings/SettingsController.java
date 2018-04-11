package com.forcetower.uefs.view.settings;

import android.content.Context;

/**
 * Created by João Paulo on 09/03/2018.
 */

public interface SettingsController {
    void logout();
    void exportToCalendar();
    void resetExportToCalendar();
    void connectToPlayGames();
    void disconnectFromPlayGames();

    Context getContext();
}
