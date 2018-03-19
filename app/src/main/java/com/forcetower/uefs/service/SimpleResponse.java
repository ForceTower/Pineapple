package com.forcetower.uefs.service;

/**
 * Created by João Paulo on 13/03/2018.
 */

public class SimpleResponse {
    public boolean error;
    public String message;
    public int update;

    public SimpleResponse(boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public boolean isUpdate() {
        return update == 1;
    }
}
