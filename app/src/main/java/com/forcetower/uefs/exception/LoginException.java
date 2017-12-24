package com.forcetower.uefs.exception;

/**
 * Created by João Paulo on 24/12/2017.
 */

public class LoginException extends RuntimeException {
    public static final int CONNECTION_ERROR = 0;
    public static final int INVALID_LOGIN = 1;

    private final int idx;

    public LoginException(int idx) {
        super();
        this.idx = idx;
    }

    public int getIdx() {
        return idx;
    }
}
