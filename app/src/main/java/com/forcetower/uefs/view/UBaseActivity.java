package com.forcetower.uefs.view;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * Created by João Paulo on 05/03/2018.
 * Base activity for all activities. Yay!
 */
public abstract class UBaseActivity extends AppCompatActivity {

    public void onCreate(@LayoutRes int layout, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
        ButterKnife.bind(this);
    }
}
