package com.forcetower.uefs.sdk;

import org.jetbrains.annotations.NotNull;
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

    //Class Keys
    private static final String CLASS_CODE_KEY = "code";
    private static final String ROOM_KEY = "room";
    private static final String START_KEY = "starts_at";
    private static final String END_KEY = "ends_at";
    private static final String CAMPUS_KEY = "campus";
    private static final String DAY_KEY = "day";
    private static final String CLASS_TYPE_KEY = "class_type";
    private static final String CLASS_NAME_KEY = "class_name";

    //Message Keys
    private static final String SENDER_KEY = "sender";
    private static final String MESSAGE_KEY = "message";
    private static final String RECEIVE_TIME_KEY = "received_at";
    private static final String CLASS_MESSAGE_KEY = "class";


    //Profile Attributes
    private String studentName;
    private List<SagresMessage> messages;
    private HashMap<String, List<SagresClassDay>> classes;

    public SagresProfile(String name, List<SagresMessage> messages, HashMap<String, List<SagresClassDay>> classes) {
        this.studentName = name;
        this.messages = messages;
        this.classes = classes;
    }

    static SagresProfile fromJSONObject(JSONObject jsonObject) throws JSONException {
        String name = jsonObject.getString(NAME_KEY);
        List<SagresMessage> messages = getMessages(jsonObject);
        HashMap<String, List<SagresClassDay>> classes = getClasses(jsonObject);

        return new SagresProfile(name, messages, classes);
    }

    private static HashMap<String,List<SagresClassDay>> getClasses(JSONObject jsonObject) {
        HashMap<String,List<SagresClassDay>> classes = new HashMap<>();
        //TODO create this
        return classes;
    }

    private static List<SagresMessage> getMessages(JSONObject jsonObject) {
        List<SagresMessage> messages = new ArrayList<>();
        //TODO create this
        return messages;
    }

    public static SagresProfile getCurrentProfile() {
        return SagresProfileManager.getInstance().getCurrentProfile();
    }

    public static void fetchProfileForCurrentAccess() {
        //TODO fetch information if logged in [This will trigger every time the user opens the app and the profile doesn't exists
        //This should also be updated in a set frequency that the user tell us
    }
}
