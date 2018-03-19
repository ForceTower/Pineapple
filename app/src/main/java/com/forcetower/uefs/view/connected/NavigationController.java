package com.forcetower.uefs.view.connected;

import android.support.design.widget.TabLayout;

/**
 * Created by João Paulo on 07/03/2018.
 */

public interface NavigationController {
    void navigateToSchedule();
    void navigateToMessages();
    void navigateToGrades();
    void navigateToDisciplines();
    void navigateToMore();
    void navigateToCalendar();
    TabLayout getTabLayout();
}
