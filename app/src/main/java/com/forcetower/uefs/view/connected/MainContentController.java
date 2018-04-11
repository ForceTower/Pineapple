package com.forcetower.uefs.view.connected;

/**
 * Created by João Paulo on 07/03/2018.
 */

public interface MainContentController {
    void navigateToSchedule();
    void navigateToMessages();
    void navigateToGrades();
    void navigateToDisciplines();
    void navigateToMore();
    void navigateToCalendar();
    void navigateToBigTray();
    void showNewScheduleError(Exception e);
}
