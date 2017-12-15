package com.forcetower.uefs.sagres_sdk.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by João Paulo on 14/12/2017.
 */

public class SagresClassTime {
    public static final String DAY_KEY = "day";
    public static final String START_KEY = "start";
    public static final String END_KEY = "end";

    private String day;
    private String start;
    private String finish;

    public SagresClassTime(String day, String start, String finish) {
        this.day = day;
        this.start = start;
        this.finish = finish;
    }

    public String getDay() {
        return day;
    }

    public String getStart() {
        return start;
    }

    public String getFinish() {
        return finish;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(DAY_KEY, day);
        jsonObject.put(START_KEY, start);
        jsonObject.put(END_KEY, finish);
        return jsonObject;
    }

    public static SagresClassTime fromJSONObject(JSONObject jsonObject) {
        String day = jsonObject.optString(DAY_KEY, "");
        String start = jsonObject.optString(START_KEY, "");
        String end = jsonObject.optString(END_KEY, "");
        return new SagresClassTime(day, start, end);
    }
}
