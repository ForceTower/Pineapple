package com.forcetower.uefs.adapters.ui;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;

/**
 * Created by João Paulo on 29/11/2017.
 */

public class NavDrawerItem {
    private boolean header;
    private String title;
    private int icon;
    private NavigationDrawerAdapter.NavDrawerItemClickListener onClickListener;
    private int tag;

    public NavDrawerItem(String title, int icon, NavigationDrawerAdapter.NavDrawerItemClickListener onClickListener) {
        this.title = title;
        this.icon = icon;
        this.onClickListener = onClickListener;
    }

    public NavDrawerItem(String title, int icon, int tag) {
        this.title = title;
        this.icon = icon;
        this.tag = tag;
    }

    public NavDrawerItem(boolean header) {
        this.header = header;
    }

    public int getTag() {
        return tag;
    }

    public NavigationDrawerAdapter.NavDrawerItemClickListener getOnClickListener() {
        return onClickListener;
    }

    public void onClick() {
        onClickListener.onClick();
    }

    public boolean isHeader() {
        return header;
    }

    @DrawableRes
    public int getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }
}
