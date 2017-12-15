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

    private String name;
    private String code;
    private String semester;
    public List<SagresClassGroup> groups;

    public SagresClassDetails(String name, String code) {
        this.name = name;
        this.code = code;
        groups = new ArrayList<>();
    }

    public void addGroup(SagresClassGroup group) {
        this.groups.add(group);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setSemester(String semester) {
        this.semester = semester;
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

    public void setGroups(List<SagresClassGroup> groups) {
        this.groups = groups;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NAME_KEY, name);
        jsonObject.put(SEMESTER_KEY, semester);
        jsonObject.put(CODE_KEY, code);

        JSONArray groupsArray = groupsToJSONArray();
        jsonObject.put(GROUPS_KEY, groupsArray);
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
        List<SagresClassGroup> groups = groupsFromJSONArray(jsonObject);

        SagresClassDetails details = new SagresClassDetails(name, code);
        details.setSemester(semester);
        details.setGroups(groups);
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
}
