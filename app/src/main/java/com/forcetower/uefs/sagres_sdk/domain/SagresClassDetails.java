package com.forcetower.uefs.sagres_sdk.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 14/12/2017.
 */

public class SagresClassDetails {
    public static final String NAME_KEY = "name";
    public static final String CODE_KEY = "code";
    public static final String SEMESTER_KEY = "semester";
    public static final String GROUPS_KEY = "groups";
    public static final String DRAFT_KEY = "draft";
    public static final String CREDITS_KEY = "credits";
    public static final String MISSED_CLASSES_KEY = "missed_classes";
    public static final String MISSED_CLASSES_INF_KEY = "missed_classes_inf";
    public static final String LAST_CLASS_KEY = "last_class";
    public static final String NEXT_CLASS_KEY = "next_class";
    public static final String SITUATION_KEY = "situation";

    private String name;
    private String code;
    private String semester;
    private String fullCredits = "0";
    private boolean draft = true;
    private List<SagresClassGroup> groups;
    private String missedClasses = "0";
    private String lastClass = "0";
    private String nextClass = "0";
    private String missedClassesInformed = "0";
    private String situation;

    public SagresClassDetails(String name, String code) {
        this.name = name.trim();
        this.code = code.trim();
        groups = new ArrayList<>();
    }

    public void addGroup(SagresClassGroup group) {
        this.groups.add(group);
    }

    public void setName(String name) {
        this.name = name.trim();
    }

    public void setCode(String code) {
        this.code = code.trim();
    }

    public void setSemester(String semester) {
        this.semester = semester.trim();
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getSemester() {
        return semester;
    }

    public List<SagresClassGroup> getGroups() {
        return groups;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }

    public void setGroups(List<SagresClassGroup> groups) {
        this.groups = groups;
    }

    public void setCredits(String credits) {
        this.fullCredits = credits.trim();
        fullCredits = fullCredits.replaceAll("[^\\d]", "").trim();
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getCredits() {
        return fullCredits;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NAME_KEY, name);
        jsonObject.put(SEMESTER_KEY, semester);
        jsonObject.put(CODE_KEY, code);
        jsonObject.put(DRAFT_KEY, draft);
        jsonObject.put(CREDITS_KEY, fullCredits);
        jsonObject.put(MISSED_CLASSES_KEY, missedClasses);
        jsonObject.put(LAST_CLASS_KEY, lastClass);
        jsonObject.put(NEXT_CLASS_KEY, nextClass);
        jsonObject.put(MISSED_CLASSES_INF_KEY, missedClassesInformed);
        jsonObject.put(SITUATION_KEY, situation);
        jsonObject.put(GROUPS_KEY, groupsToJSONArray());
        return jsonObject;
    }

    private JSONArray groupsToJSONArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (SagresClassGroup group : groups) {
            jsonArray.put(group.toJSONObject());
        }
        return jsonArray;
    }

    public static SagresClassDetails fromJSONObject(JSONObject jsonObject) throws JSONException {
        String name = jsonObject.getString(NAME_KEY);
        String code = jsonObject.getString(CODE_KEY);
        String semester = jsonObject.getString(SEMESTER_KEY);
        boolean draft = jsonObject.getBoolean(DRAFT_KEY);
        String credits = jsonObject.optString(CREDITS_KEY, "0");
        String missedClasses = jsonObject.optString(MISSED_CLASSES_KEY, "0");
        String lastClass = jsonObject.optString(LAST_CLASS_KEY, "");
        String nextClass = jsonObject.optString(NEXT_CLASS_KEY, "");
        String missedClassesInformed = jsonObject.optString(MISSED_CLASSES_INF_KEY, "0");
        String situation = jsonObject.optString(SITUATION_KEY, null);
        List<SagresClassGroup> groups = groupsFromJSONArray(jsonObject);

        SagresClassDetails details = new SagresClassDetails(name, code);
        details.setSemester(semester);
        details.setGroups(groups);
        details.setDraft(draft);
        details.setCredits(credits);
        details.setMissedClasses(missedClasses);
        details.setLastClass(lastClass);
        details.setNextClass(nextClass);
        details.setMissedClassesInformed(missedClassesInformed);
        details.setSituation(situation);
        return details;
    }

    private static List<SagresClassGroup> groupsFromJSONArray(JSONObject jsonObject) throws JSONException {
        List<SagresClassGroup> groups = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray(GROUPS_KEY);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject group = jsonArray.getJSONObject(i);
            groups.add(SagresClassGroup.fromJSONObject(group));
        }
        return groups;
    }

    public void setMissedClasses(String missedClasses) {
        this.missedClasses = missedClasses;
    }

    public void setLastClass(String lastClass) {
        this.lastClass = lastClass;
    }

    public void setNextClass(String nextClass) {
        this.nextClass = nextClass;
    }

    public void setMissedClassesInformed(String missedClassesInformed) {
        this.missedClassesInformed = missedClassesInformed;
    }

    public String getMissedClasses() {
        return missedClasses;
    }

    public String getLastClass() {
        return lastClass;
    }

    public String getNextClass() {
        return nextClass;
    }

    public String getMissedClassesInformed() {
        return missedClassesInformed;
    }
}
