package com.forcetower.uefs.sagres_sdk.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 25/11/2017.
 */
public class SagresGrade {
    private static final String CLASS_CODE_KEY = "class_code";
    private static final String CLASS_NAME_KEY = "class_name";
    private static final String FINAL_SCORE_KEY = "score";
    private static final String SECTIONS_KEY = "sections";

    private List<GradeSection> sections;
    private String finalScore;
    private String className;
    private String classCode;

    public SagresGrade(String singleName) {
        String[] names = singleName.split("-");
        this.classCode = names[0].trim();

        String name = names[1].trim();
        if (name.contains("(")) {
            name = name.substring(0, name.lastIndexOf("("));
        }
        this.className = name;
        sections = new ArrayList<>();
    }

    public SagresGrade(String className, String classCode) {
        this.className = className;
        this.classCode = classCode;
        sections = new ArrayList<>();
    }

    private SagresGrade(String className, String classCode, String finalScore, List<GradeSection> sections) {
        this.className = className;
        this.classCode = classCode;
        this.finalScore = finalScore;
        this.sections = sections;
    }

    public void addSection(GradeSection section) {
        this.sections.add(section);
    }

    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }

    public List<GradeSection> getSections() {
        return sections;
    }

    public void setSections(List<GradeSection> sections) {
        this.sections = sections;
    }

    public String getFinalScore() {
        return finalScore;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(CLASS_NAME_KEY, className);
        jsonObject.put(CLASS_CODE_KEY, classCode);
        jsonObject.put(FINAL_SCORE_KEY, finalScore);
        jsonObject.put(SECTIONS_KEY, sectionsToJSONArray());
        return jsonObject;
    }

    private JSONArray sectionsToJSONArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (GradeSection section : sections) {
            jsonArray.put(section.toJSONObject());
        }

        return jsonArray;
    }

    public static SagresGrade fromJSONObject(JSONObject jsonObject) throws JSONException {
        String className = jsonObject.getString(CLASS_NAME_KEY);
        String classCode = jsonObject.getString(CLASS_CODE_KEY);
        String finalScore = jsonObject.getString(FINAL_SCORE_KEY);
        List<GradeSection> sections = sectionsFromJSONArray(jsonObject.getJSONArray(SECTIONS_KEY));
        return new SagresGrade(className, classCode, finalScore, sections);
    }

    private static List<GradeSection> sectionsFromJSONArray(JSONArray jsonArray) throws JSONException {
        List<GradeSection> sections = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            sections.add(GradeSection.fromJSONObject(jsonArray.getJSONObject(i)));
        }
        return sections;
    }

    public GradeSection findSection(String name) {
        for (GradeSection section : getSections()) {
            if (section.getName().equalsIgnoreCase(name))
                return section;
        }
        return null;
    }
}
