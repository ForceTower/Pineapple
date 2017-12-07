package com.forcetower.uefs.sagres_sdk.exception;

/**
 * Created by João Paulo on 18/11/2017.
 */

public class SagresLoginException extends Exception {
    private boolean failedLogin;
    private boolean failedConnection;

    public SagresLoginException(String message) {
        super(message);
    }

    public SagresLoginException(boolean failedLogin, boolean failedConnection, String message) {
        super(message);
        this.failedConnection = failedConnection;
        this.failedLogin = failedLogin;
    }

    public boolean failedConnection() {
        return failedConnection;
    }

    public boolean failedLogin() {
        return failedLogin;
    }
}
