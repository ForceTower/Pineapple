package com.forcetower.uefs.view.login;

/**
 * Created by João Paulo on 18/11/2017.
 */

public interface LoginViewCallback {
    void onLoginClicked(String username, String password);
    void onLoginFailed(Throwable throwable);
    void onLoginSuccess();
}