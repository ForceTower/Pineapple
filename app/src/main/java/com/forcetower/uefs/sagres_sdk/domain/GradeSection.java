package com.forcetower.uefs.sagres_sdk.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 25/11/2017.
 */
public class GradeSection {
    private static final String NAME_KEY = "name";
    private static final String GRADES_KEY = "grades";

    private String name;
    private List<GradeInfo> grades;

    public GradeSection(String name) {
        this.name = name;
        grades = new ArrayList<>();
    }

    public GradeSection(String name, List<GradeInfo> grades) {
        this.name = name;
        this.grades = grades;
    }

    public void addGradeInfo(GradeInfo info) {
        grades.add(info);
    }

    public String getName() {
        return name;
    }

    public List<GradeInfo> getGrades() {
        return grades;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NAME_KEY, name);
        jsonObject.put(GRADES_KEY, gradesToJSONArray());
        return jsonObject;
    }

    private JSONArray gradesToJSONArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (GradeInfo gradeInfo : grades) {
            jsonArray.put(gradeInfo.toJSONObject());
        }
        return jsonArray;
    }

    public static GradeSection fromJSONObject(JSONObject jsonObject) throws JSONException {
        String name = jsonObject.optString(NAME_KEY, "Notas");
        List<GradeInfo> grades = gradesFromJSONArray(jsonObject.getJSONArray(GRADES_KEY));
        return new GradeSection(name, grades);
    }

    private static List<GradeInfo> gradesFromJSONArray(JSONArray jsonArray) throws JSONException {
        List<GradeInfo> grades = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            grades.add(GradeInfo.fromJSONObject(jsonObject));
        }
        return grades;
    }
}
