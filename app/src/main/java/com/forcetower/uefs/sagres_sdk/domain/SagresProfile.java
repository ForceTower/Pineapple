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

    //Profile Attributes
    private String studentName;
    private List<SagresMessage> messages;
    private HashMap<String, List<SagresClassDay>> classes;

    public SagresProfile(String name, List<SagresMessage> messages, HashMap<String, List<SagresClassDay>> classes) {
        this.studentName = name;
        this.messages = messages;
        this.classes = classes;
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

        return new SagresProfile(name, messages, classes);
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
}
