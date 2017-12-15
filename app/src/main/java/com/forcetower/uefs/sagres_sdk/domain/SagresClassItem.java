package com.forcetower.uefs.sagres_sdk.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by João Paulo on 14/12/2017.
 */

public class SagresClassItem {
    public static final String NUMBER_KEY = "number";
    public static final String SITUATION_KEY = "situation";
    public static final String SUBJECT_KEY = "subject";
    public static final String DATE_KEY = "date";
    public static final String MATERIALS_KEY = "materials";

    private String number;
    private String situation;
    private String subject;
    private String date;
    private String numberOfMaterials;

    public SagresClassItem(String number, String situation, String subject, String date, String numberOfMaterials) {
        this.number = number;
        this.situation = situation;
        this.subject = subject;
        this.date = date;
        this.numberOfMaterials = numberOfMaterials;
    }

    public String getNumber() {
        return number;
    }

    public String getSituation() {
        return situation;
    }

    public String getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    public String getNumberOfMaterials() {
        return numberOfMaterials;
    }

    public JSONObject toJSONObject() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NUMBER_KEY, number);
        jsonObject.put(SITUATION_KEY, situation);
        jsonObject.put(SUBJECT_KEY, subject);
        jsonObject.put(DATE_KEY, date);
        jsonObject.put(MATERIALS_KEY, numberOfMaterials);
        return jsonObject;
    }

    public static SagresClassItem fromJSONObject(JSONObject jsonObject) throws JSONException {
        String number = jsonObject.optString(NUMBER_KEY, "");
        String situation = jsonObject.optString(SITUATION_KEY, "");
        String subject = jsonObject.optString(SUBJECT_KEY, "");
        String date = jsonObject.optString(DATE_KEY, "");
        String materials = jsonObject.optString(MATERIALS_KEY, "0");
        return new SagresClassItem(number, situation, subject, date, materials);
    }
}
