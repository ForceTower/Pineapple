package com.forcetower.uefs.sagres_sdk.domain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 14/12/2017.
 */

public class SagresClassGroup {
    public static final String TEACHER_KEY = "teacher";
    public static final String TYPE_KEY = "type";
    public static final String CREDITS_KEY = "credits";
    public static final String MISS_LIMIT_KEY = "miss_limit";
    public static final String CLASS_PERIOD_KEY = "class_period";
    public static final String DEPARTMENT_KEY = "department";
    public static final String CLASS_TIME_KEY = "class_time";
    public static final String CLASSES_KEY = "classes";

    private String teacher;
    private String type;
    private String credits;
    private String missLimit;
    private String classPeriod;
    private String department;
    private List<SagresClassTime> classTimeList;
    private List<SagresClassItem> classes;

    public SagresClassGroup(String teacher, String type, String credits, String missLimit, String classPeriod, String department) {
        this.teacher = teacher;
        this.type = type;
        this.credits = credits;
        this.missLimit = missLimit;
        this.classPeriod = classPeriod;
        this.department = department;
        classTimeList = new ArrayList<>();
        classes = new ArrayList<>();
    }

    public void setClassTimeList(List<SagresClassTime> classTimeList) {
        this.classTimeList = classTimeList;
    }

    public void setClasses(List<SagresClassItem> classes) {
        this.classes = classes;
    }

    public JSONObject toJSONObject() throws JSONException{
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(TEACHER_KEY, teacher);
        jsonObject.put(TYPE_KEY, type);
        jsonObject.put(CREDITS_KEY, credits);
        jsonObject.put(MISS_LIMIT_KEY, missLimit);
        jsonObject.put(CLASS_PERIOD_KEY, classPeriod);
        jsonObject.put(DEPARTMENT_KEY, department);
        jsonObject.put(CLASS_TIME_KEY, classTimeToJSONArray());
        jsonObject.put(CLASSES_KEY, classesToJSONArray());
        return jsonObject;
    }

    private JSONArray classesToJSONArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (SagresClassItem classItem : classes) {
            jsonArray.put(classItem.toJSONObject());
        }
        return jsonArray;
    }

    private JSONArray classTimeToJSONArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for (SagresClassTime classTime : classTimeList) {
            jsonArray.put(classTime.toJSONObject());
        }
        return jsonArray;
    }

    public static SagresClassGroup fromJSONObject(JSONObject jsonObject) throws JSONException {
        String teacher = jsonObject.optString(TEACHER_KEY, "");
        String type = jsonObject.optString(TYPE_KEY, "");
        String credits = jsonObject.optString(CREDITS_KEY, "");
        String missLimit = jsonObject.optString(MISS_LIMIT_KEY, "");
        String classPeriod = jsonObject.optString(CLASS_PERIOD_KEY, "");
        String department = jsonObject.optString(DEPARTMENT_KEY, "");

        List<SagresClassTime> classTimeList = classTimeFromJSONArray(jsonObject);
        List<SagresClassItem> classes = classesFromJSONArray(jsonObject);

        SagresClassGroup classGroup = new SagresClassGroup(teacher, type, credits, missLimit, classPeriod, department);
        classGroup.setClassTimeList(classTimeList);
        classGroup.setClasses(classes);
        return classGroup;
    }

    private static List<SagresClassItem> classesFromJSONArray(JSONObject jsonObject) throws JSONException {
        List<SagresClassItem> classes = new ArrayList<>();
        JSONArray classesArray = jsonObject.getJSONArray(CLASSES_KEY);
        for (int i = 0; i < classesArray.length(); i++) {
            JSONObject object = classesArray.getJSONObject(i);
            SagresClassItem classItem = SagresClassItem.fromJSONObject(object);
            classes.add(classItem);
        }
        return classes;
    }

    private static List<SagresClassTime> classTimeFromJSONArray(JSONObject jsonObject) throws JSONException {
        List<SagresClassTime> classTimeList = new ArrayList<>();
        JSONArray classesArray = jsonObject.getJSONArray(CLASS_TIME_KEY);
        for (int i = 0; i < classesArray.length(); i++) {
            JSONObject object = classesArray.getJSONObject(i);
            SagresClassTime classTime = SagresClassTime.fromJSONObject(object);
            classTimeList.add(classTime);
        }
        return classTimeList;
    }
}
