package com.forcetower.uefs.view.logged;

import android.graphics.Bitmap;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;

/**
 * Created by João Paulo on 11/04/2018.
 */
public interface ActivityController {
    TabLayout getTabLayout();
    NavigationController getNavigationController();
    void changeTitle(int idRes);
    void selectItemFromNavigation(int idRes);
    void onProfileImageChanged(Bitmap bitmap);
}
