package com.forcetower.uefs.service;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import retrofit2.Response;
import timber.log.Timber;

/**
 * Common class used by API responses.
 * @param <T>
 */
public class ApiResponse<T> {
    public final int code;
    @Nullable
    public final T body;
    @Nullable
    public final String errorMessage;
    @Nullable
    public final ActionError actionError;

    public ApiResponse(Throwable error) {
        code = 500;
        body = null;
        errorMessage = error.getMessage();
        actionError = null;
    }

    public ApiResponse(Response<T> response) {
        code = response.code();

        if(response.isSuccessful()) {
            body = response.body();
            errorMessage = null;
            actionError = null;
        } else {
            String message = null;
            ActionError aError = null;
            if (response.errorBody() != null) {
                try {
                    message = response.errorBody().string();
                    if (message != null) aError = new Gson().fromJson(message, ActionError.class);
                } catch (Exception ignored) {
                    Timber.e(ignored, "error while parsing response");
                }
            }
            if (message == null || message.trim().length() == 0) {
                message = response.message();
            }
            actionError = aError;
            errorMessage = message;
            body = null;
        }
    }

    public boolean isSuccessful() {
        return code >= 200 && code < 300;
    }
}