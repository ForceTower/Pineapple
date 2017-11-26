package com.forcetower.uefs.sagres_sdk.domain;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by João Paulo on 25/11/2017.
 */
public class GradeInfo {
    private static final String EVALUATION_NAME_KEY = "evaluation_name";
    private static final String GRADE_KEY = "grade";
    private static final String DATE_KEY = "date";

    private String evalName;
    private String grade;
    private String date;

    public GradeInfo(String evalName, String grade, String date) {
        this.evalName = evalName;
        this.grade = grade;
        this.date = date;
    }

    public String getEvaluationName() {
        return evalName;
    }

    public String getGrade() {
        return grade;
    }

    public String getDate() {
        return date;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(EVALUATION_NAME_KEY, evalName);
        jsonObject.put(GRADE_KEY, grade);
        jsonObject.put(DATE_KEY, date);
        return jsonObject;
    }

    public static GradeInfo fromJSONObject(JSONObject jsonObject) throws JSONException {
        String evalName = jsonObject.getString(EVALUATION_NAME_KEY);
        String grade = jsonObject.getString(GRADE_KEY);
        String date = jsonObject.getString(DATE_KEY);

        return new GradeInfo(evalName, grade, date);
    }
}
