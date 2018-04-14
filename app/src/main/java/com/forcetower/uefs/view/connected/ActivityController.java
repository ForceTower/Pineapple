package com.forcetower.uefs.view.connected;

import android.graphics.Bitmap;
import android.support.design.widget.TabLayout;

/**
 * Created by João Paulo on 11/04/2018.
 */
public interface ActivityController {
    TabLayout getTabLayout();
    NavigationController getNavigationController();
    void changeTitle(int idRes);
    void selectItemFromNavigation(int idRes);
    void onProfileImageChanged(Bitmap bitmap);
    void showNewScheduleError(Exception ex);
}
