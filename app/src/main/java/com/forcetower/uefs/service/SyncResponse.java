package com.forcetower.uefs.service;

public class SyncResponse {
    public int update;
    public int count;

    public SyncResponse(int update) {
        this.update = update;
    }

    public boolean isUpdate() {
        return update == 1;
    }

    public void setUpdate(int update) {
        this.update = update;
    }
}