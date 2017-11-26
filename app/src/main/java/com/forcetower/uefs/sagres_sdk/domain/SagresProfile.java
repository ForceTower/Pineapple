package com.forcetower.uefs.sagres_sdk.domain;

import com.forcetower.uefs.sagres_sdk.exception.SagresLoginException;
import com.forcetower.uefs.sagres_sdk.managers.SagresProfileManager;
import com.forcetower.uefs.sagres_sdk.utility.SagresDayUtils;
import com.forcetower.uefs.sagres_sdk.utility.SagresUtility;
import com.forcetower.uefs.sagres_sdk.validators.Validate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by João Paulo on 17/11/2017.
 */

public class SagresProfile {
    //General Keys
    private static final String CLASSES_KEY = "classes";
    private static final String MESSAGES_KEY = "messages";
    private static final String NAME_KEY = "name";
    private static final String GRADES_KEY = "grades";

    //Profile Attributes
    private String studentName;
    private List<SagresMessage> messages;
    private HashMap<String, List<SagresClassDay>> classes;
    private HashMap<String, SagresGrade> grades;

    public SagresProfile(String name, List<SagresMessage> messages, HashMap<String, List<SagresClassDay>> classes) {
        this.studentName = name;
        this.messages = messages;
        this.classes = classes;
    }

    public SagresProfile(String name, List<SagresMessage> messages, HashMap<String, List<SagresClassDay>> classes, HashMap<String, SagresGrade> grades) {
        this.studentName = name;
        this.messages = messages;
        this.classes = classes;
        this.grades = grades;
    }

    public static SagresProfile getCurrentProfile() {
        return SagresProfileManager.getInstance().getCurrentProfile();
    }

    public static void setCurrentProfile(SagresProfile profile) {
        SagresProfileManager.getInstance().setCurrentProfile(profile);
    }

    public static void fetchProfileForCurrentAccess() {
        SagresAccess access = SagresAccess.getCurrentAccess();
        if (access == null) {
            setCurrentProfile(null);
            return;
        }

        SagresUtility.getInformationFromUserWithCacheAsync(access, new SagresUtility.AllInformationFetchWithCacheCallback() {
            @Override
            public void onSuccess(SagresProfile profile) {
                System.out.println("Profile fetch");
                setCurrentProfile(profile);
            }

            @Override
            public void onFailure(SagresLoginException e) {
                e.printStackTrace();
            }

            @Override
            public void onLoginSuccess() {
            }
        });
    }

    public static void fetchProfileForSagresAccess(SagresAccess access, SagresUtility.AllInformationFetchWithCacheCallback callback) {
        setCurrentProfile(null);
        SagresUtility.getInformationFromUserWithCacheAsync(access, callback);
    }

    public static SagresProfile fromJSONObject(JSONObject jsonObject) throws JSONException {
        String name = jsonObject.getString(NAME_KEY);
        List<SagresMessage> messages = getMessages(jsonObject);
        HashMap<String, List<SagresClassDay>> classes = getClasses(jsonObject);
        HashMap<String, SagresGrade> grades = getGrades(jsonObject);

        return new SagresProfile(name, messages, classes, grades);
    }

    private static HashMap<String, List<SagresClassDay>> getClasses(JSONObject jsonObject) throws JSONException {
        HashMap<String, List<SagresClassDay>> classes = new HashMap<>();
        JSONObject classesObject = jsonObject.getJSONObject(CLASSES_KEY);

        for (int i = 1; i <= 7; i++) {
            List<SagresClassDay> classesDay = new ArrayList<>();
            String day = SagresDayUtils.getDayOfWeek(i);
            JSONArray dayObject = classesObject.getJSONArray(day);

            for (int j = 0; j < dayObject.length(); j++) {
                SagresClassDay classDay = SagresClassDay.fromJSONObject(dayObject.getJSONObject(j));
                classesDay.add(classDay);
            }
            classes.put(day, classesDay);
        }
        return classes;
    }

    private static List<SagresMessage> getMessages(JSONObject jsonObject) throws JSONException {
        List<SagresMessage> messages = new ArrayList<>();
        JSONArray messagesArray = jsonObject.getJSONArray(MESSAGES_KEY);

        for (int i = 0; i < messagesArray.length(); i++) {
            JSONObject messageObject = messagesArray.getJSONObject(i);
            SagresMessage message = SagresMessage.fromJSONObject(messageObject);
            messages.add(message);
        }
        return messages;
    }

    private static HashMap<String, SagresGrade> getGrades(JSONObject jsonObject) throws JSONException {
        List<SagresGrade> gradesList = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray(GRADES_KEY);
        for (int i = 0; i < jsonArray.length(); i++) {
            gradesList.add(SagresGrade.fromJSONObject(jsonArray.getJSONObject(i)));
        }

        HashMap<String, SagresGrade> grades = new HashMap<>();
        for (SagresGrade grade : gradesList) {
            grades.put(grade.getClassCode(), grade);
        }
        return grades;
    }

    public static void asyncFetchProfileInformationWithCallback(SagresUtility.AsyncFetchProfileInformationCallback callback) {
        SagresUtility.getProfileInformationAsyncWithCallback(callback);
    }

    public HashMap<String, List<SagresClassDay>> getClasses() {
        return classes;
    }

    public List<SagresMessage> getMessages() {
        return messages;
    }

    public JSONObject toJSONObject() throws JSONException {
        Validate.notNullFields(this);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(NAME_KEY, studentName);

        JSONObject classesObject = classesToJSONObject();
        jsonObject.put(CLASSES_KEY, classesObject);

        JSONArray messagesArray = messagesToJSONObject();
        jsonObject.put(MESSAGES_KEY, messagesArray);

        JSONArray gradesArray = gradesToJSONArray();
        jsonObject.put(GRADES_KEY, gradesArray);

        return jsonObject;
    }

    private JSONObject classesToJSONObject() throws JSONException {
        JSONObject classesObject = new JSONObject();

        for (String day : classes.keySet()) {
            JSONArray dayObject = new JSONArray();
            for (SagresClassDay classesDay : classes.get(day)) {
                JSONObject classDayObject = classesDay.toJSONObject();
                dayObject.put(classDayObject);
            }
            classesObject.put(day, dayObject);
        }

        return classesObject;
    }

    private JSONArray messagesToJSONObject() throws JSONException {
        JSONArray messagesArray = new JSONArray();

        for (SagresMessage message : messages) {
            JSONObject messageObject = message.toJSONObject();
            messagesArray.put(messageObject);
        }

        return messagesArray;
    }

    private JSONArray gradesToJSONArray() throws JSONException{
        JSONArray gradesArray = new JSONArray();

        for (String code : grades.keySet()) {
            SagresGrade grade = grades.get(code);
            JSONObject gradeObject = grade.toJSONObject();
            gradesArray.put(gradeObject);
        }

        return gradesArray;
    }

    public void updateInformation(String studentName, List<SagresMessage> messages, HashMap<String, List<SagresClassDay>> classes) {
        this.studentName = studentName;
        this.classes = classes;
        mergeMessages(messages);
        setCurrentProfile(this);
    }

    private void mergeMessages(List<SagresMessage> newMessages) {
        for (SagresMessage message : newMessages) {
            if (!messages.contains(message))
                messages.add(0, message);
        }
    }

    public HashMap<String, SagresGrade> getGrades() {
        return grades;
    }

    public void placeNewGrades(HashMap<String, SagresGrade> grades) {
        this.grades = grades;
        setCurrentProfile(this);
        //TODO create a logic to know if a grad changed
    }
}
