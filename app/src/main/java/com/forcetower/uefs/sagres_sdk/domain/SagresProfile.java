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
import java.util.Map;

/**
 * Created by João Paulo on 17/11/2017.
 */

public class SagresProfile {
    //General Keys
    private static final String CLASSES_KEY = "classes";
    private static final String MESSAGES_KEY = "messages";
    private static final String NAME_KEY = "name";
    private static final String GRADES_KEY = "grades";
    private static final String ALL_GRADES_KEY = "all_grades";
    private static final String CALENDAR_KEY = "calendar";

    //Profile Attributes
    private String studentName;
    private List<SagresMessage> messages;
    private HashMap<String, List<SagresClassDay>> classes;
    private HashMap<String, SagresGrade> grades;
    private HashMap<SagresSemester, List<SagresGrade>> allSemestersGrades;
    private List<SagresCalendarItem> calendar;

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

    /**
     * Fetches full information of user;
     * This should only be called when the user logs in or in case of full profile restore
     */
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

    /**
     * Fetches full information of user;
     * This should only be called when the user logs in or in case of full profile restore
     * @param access Access to be used [Username, Password]
     * @param callback information about the fetch, can be null
     */
    public static void fetchProfileForSagresAccess(SagresAccess access, SagresUtility.AllInformationFetchWithCacheCallback callback) {
        setCurrentProfile(null);
        SagresUtility.getInformationFromUserWithCacheAsync(access, callback);
    }

    public static SagresProfile fromJSONObject(JSONObject jsonObject) throws JSONException {
        String name = jsonObject.getString(NAME_KEY);
        List<SagresMessage> messages = getMessages(jsonObject);
        HashMap<String, List<SagresClassDay>> classes = getClasses(jsonObject);
        HashMap<String, SagresGrade> grades = getGrades(jsonObject);
        HashMap<SagresSemester, List<SagresGrade>> allGrades = getAllGrades(jsonObject);
        List<SagresCalendarItem> calendar = getCalendar(jsonObject);

        SagresProfile profile = new SagresProfile(name, messages, classes, grades);
        profile.setAllSemestersGrades(allGrades);
        profile.setCalendar(calendar);
        return profile;
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

    private static List<SagresGrade> getGradesWithoutTransformation(JSONArray jsonArray) throws JSONException {
        List<SagresGrade> gradesList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            gradesList.add(SagresGrade.fromJSONObject(jsonArray.getJSONObject(i)));
        }

        return gradesList;
    }

    private static HashMap<SagresSemester,List<SagresGrade>> getAllGrades(JSONObject jsonObject) throws JSONException {
        HashMap<SagresSemester,List<SagresGrade>> allGrades = new HashMap<>();
        JSONArray jsonArray = jsonObject.getJSONArray(ALL_GRADES_KEY);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String semesterCode = object.getString(SagresSemester.SEMESTER_CODE_KEY);
            String semesterName = object.getString(SagresSemester.SEMESTER_NAME_KEY);

            JSONArray classes = object.getJSONArray(GRADES_KEY);
            List<SagresGrade> grades = getGradesWithoutTransformation(classes);
            SagresSemester semester = new SagresSemester(semesterCode, semesterName);

            allGrades.put(semester, grades);
        }

        return allGrades;
    }

    private static List<SagresCalendarItem> getCalendar(JSONObject jsonObject) throws JSONException {
        List<SagresCalendarItem> calendar = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray(CALENDAR_KEY);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            calendar.add(SagresCalendarItem.fromJSONObject(object));
        }

        return calendar;
    }

    /**
     * Fetch simple user data, can be called all the time
     * @param callback information about fetch
     */
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

        JSONArray allGradesArray = allGradesToJSONArray();
        jsonObject.put(ALL_GRADES_KEY, allGradesArray);

        JSONArray calendarArray = calendarToJSONArray();
        jsonObject.put(CALENDAR_KEY, calendarArray);

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

    private JSONArray gradesToJSONArray(List<SagresGrade> grades) throws JSONException{
        JSONArray gradesArray = new JSONArray();

        for (SagresGrade grade : grades) {
            JSONObject gradeObject = grade.toJSONObject();
            gradesArray.put(gradeObject);
        }

        return gradesArray;
    }

    private JSONArray allGradesToJSONArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (Map.Entry<SagresSemester, List<SagresGrade>> entry : allSemestersGrades.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            SagresSemester key = entry.getKey();
            List<SagresGrade> grades = entry.getValue();
            jsonObject.put(SagresSemester.SEMESTER_NAME_KEY, key.getName());
            jsonObject.put(SagresSemester.SEMESTER_CODE_KEY, key.getSemesterCode());
            jsonObject.put(GRADES_KEY, gradesToJSONArray(grades));

            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

    private JSONArray calendarToJSONArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (SagresCalendarItem item : calendar) {
            jsonArray.put(item.toJSONObject());
        }

        return jsonArray;
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

    public void setAllSemestersGrades(HashMap<SagresSemester, List<SagresGrade>> allSemestersGrades) {
        this.allSemestersGrades = allSemestersGrades;
    }

    public HashMap<SagresSemester, List<SagresGrade>> getAllSemestersGrades() {
        return allSemestersGrades;
    }

    public List<SagresGrade> getGradesOfSemester(String semester) {
        for (Map.Entry<SagresSemester, List<SagresGrade>> entry : allSemestersGrades.entrySet()) {
            if (entry.getKey().getName().equals(semester))
                return entry.getValue();
        }
        return null;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setCalendar(List<SagresCalendarItem> calendar) {
        this.calendar = calendar;
    }

    public List<SagresCalendarItem> getCalendar() {
        return calendar;
    }
}
