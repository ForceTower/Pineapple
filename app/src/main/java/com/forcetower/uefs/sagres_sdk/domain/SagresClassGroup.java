package com.forcetower.uefs.sagres_sdk.domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by João Paulo on 14/12/2017.
 */

public class SagresClassGroup {
    private String teacher;
    private String type;
    private String credits;
    private String missLimit;
    private String classPeriod;
    private String department;
    private List<SagresClassTime> classTimeList;
    private List<SagresClassItem> classes;

    public JSONObject toJSONObject() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }
}
