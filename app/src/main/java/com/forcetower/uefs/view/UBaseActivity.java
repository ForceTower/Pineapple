package com.forcetower.uefs.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import com.forcetower.uefs.R;

import butterknife.ButterKnife;

/**
 * Created by João Paulo on 05/03/2018.
 * Base activity for all activities. Yay!
 */
public abstract class UBaseActivity extends AppCompatActivity {

    public void onCreate(@LayoutRes int layout, Bundle savedInstanceState) {
        themeSelector();
        super.onCreate(savedInstanceState);
        setContentView(layout);
        ButterKnife.bind(this);
    }

    private void themeSelector() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preferences.getString("unes_selected_theme", "default_theme");

        if (!theme.equalsIgnoreCase("default_theme")) {

            if (theme.equalsIgnoreCase("ellen_theme"))
                setTheme(R.style.AppThemeEllen1);

            else if (theme.equalsIgnoreCase("random_1_theme"))
                setTheme(R.style.AppThemeForce1);

            else if (theme.equalsIgnoreCase("random_2_theme"))
                setTheme(R.style.AppThemeForce2);

            else if (theme.equalsIgnoreCase("gray_1_theme"))
                setTheme(R.style.AppThemeGray1);
        }
    }
}
