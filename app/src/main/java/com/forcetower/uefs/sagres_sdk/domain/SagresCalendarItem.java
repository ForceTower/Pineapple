package com.forcetower.uefs.sagres_sdk.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by João Paulo on 02/12/2017.
 */

public class SagresCalendarItem {
    private static final String DAY_KEY = "day";
    private static final String LAST_DAY_KEY = "last_day";
    private static final String MESSAGE_KEY = "message";

    private String day;
    private String lastDay;
    private String message;

    public SagresCalendarItem(String day, String lastDay, String message) {
        this.day = day;
        this.lastDay = lastDay;
        this.message = message;
    }

    public String getDay() {
        return day;
    }

    public String getMessage() {
        return message;
    }

    public String getLastDay() {
        return lastDay;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject object = new JSONObject();
        object.put(DAY_KEY, day);
        object.put(LAST_DAY_KEY, lastDay);
        object.put(MESSAGE_KEY, message);
        return object;
    }

    public static SagresCalendarItem fromJSONObject(JSONObject jsonObject) throws JSONException {
        String day = jsonObject.getString(DAY_KEY);
        String last = jsonObject.optString(LAST_DAY_KEY);
        String message = jsonObject.getString(MESSAGE_KEY);
        return new SagresCalendarItem(day, last, message);
    }
}
