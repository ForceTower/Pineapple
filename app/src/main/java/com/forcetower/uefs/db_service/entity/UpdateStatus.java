package com.forcetower.uefs.db_service.entity;

import androidx.room.Entity;

/**
 * Created by João Paulo on 29/04/2018.
 */
public class UpdateStatus {
    private boolean manager;
    private boolean alarm;
    private int count;

    public UpdateStatus(boolean manager, boolean alarm, int count) {
        this.manager = manager;
        this.alarm = alarm;
        this.count = count;
    }

    public boolean isManager() {
        return manager;
    }

    public void setManager(boolean manager) {
        this.manager = manager;
    }

    public boolean isAlarm() {
        return alarm;
    }

    public void setAlarm(boolean alarm) {
        this.alarm = alarm;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
