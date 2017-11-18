package com.forcetower.uefs.sdk;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by João Paulo on 17/11/2017.
 */

public class SagresAccess {
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";

    private final String username;
    private final String password;

    private SagresAccess(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static SagresAccess getCurrentAccess() {
        return SagresAccessManager.getInstance().getCurrentAccessCredentials();
    }

    public static void setCurrentAccess(SagresAccess access) {
        SagresAccessManager.getInstance().setCurrentCredentials(access);
    }

    public static SagresAccess createFromJSONObject(JSONObject jsonObject) throws JSONException {
        String username = jsonObject.getString(USERNAME_KEY);
        String password = jsonObject.getString(PASSWORD_KEY);

        return new SagresAccess(username, password);
    }
}
