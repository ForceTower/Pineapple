package com.forcetower.uefs.service;

/**
 * Created by João Paulo on 29/04/2018.
 */
public class ActionError {
    private int code;
    private String message;
    private boolean error;

    public ActionError(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
