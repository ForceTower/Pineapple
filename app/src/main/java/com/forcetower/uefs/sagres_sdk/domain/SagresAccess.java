package com.forcetower.uefs.sagres_sdk.domain;

import com.forcetower.uefs.sagres_sdk.managers.SagresAccessManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by João Paulo on 17/11/2017.
 */

public class SagresAccess {
    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String LAST_LOGIN_KEY = "last_login";

    private final String username;
    private final String password;
    private String lastLogin;
    private Calendar lastLoginCalendar;

    public SagresAccess(String username, String password) {
        this(username, password, Calendar.getInstance().getTime().toString());
    }

    public SagresAccess(String username, String password, String lastLogin) {
        this.username = username;
        this.password = password;
        this.lastLogin = lastLogin;
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
        String lastLogin = jsonObject.getString(LAST_LOGIN_KEY);

        return new SagresAccess(username, password, lastLogin);
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(USERNAME_KEY, username);
        jsonObject.put(PASSWORD_KEY, password);
        jsonObject.put(LAST_LOGIN_KEY, lastLogin);
        return jsonObject;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
