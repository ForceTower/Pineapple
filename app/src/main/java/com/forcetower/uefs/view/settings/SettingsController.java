package com.forcetower.uefs.view.settings;

import android.content.Context;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;

/**
 * Created by João Paulo on 09/03/2018.
 */

public interface SettingsController {
    void exportToCalendar();
    void resetExportToCalendar();

    Context getContext();
}
