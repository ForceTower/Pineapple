package com.forcetower.uefs.service;

import com.google.gson.annotations.SerializedName;

public class SyncResponse {
    public int update;
    @SerializedName(value = "second_update")
    public int secondUpdate;
    public int count;

    public SyncResponse(int update) {
        this.update = update;
    }

    public boolean isUpdate() {
        return update == 1;
    }

    public boolean isAlarmEnabled() {
        return secondUpdate > 1 && update >= 1;
    }

    public void setUpdate(int update) {
        this.update = update;
    }
}