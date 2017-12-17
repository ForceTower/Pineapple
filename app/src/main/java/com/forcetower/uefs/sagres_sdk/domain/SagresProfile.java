package com.forcetower.uefs.sagres_sdk.domain;

import android.util.Log;

import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
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
    private static final String SCORE_KEY = "score";
    private static final String CLASS_DETAILS_KEY = "class_details";

    //Profile Attributes
    private String studentName;
    private String score;
    private List<SagresMessage> messages;
    private HashMap<String, List<SagresClassDay>> classes;
    private HashMap<String, SagresGrade> grades;
    private HashMap<SagresSemester, List<SagresGrade>> allSemestersGrades;
    private List<SagresCalendarItem> calendar;
    private List<SagresClassDetails> classDetails;

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
        String score = jsonObject.getString(SCORE_KEY);
        List<SagresMessage> messages = getMessages(jsonObject);
        HashMap<String, List<SagresClassDay>> classes = getClasses(jsonObject);
        HashMap<String, SagresGrade> grades = getGrades(jsonObject);
        HashMap<SagresSemester, List<SagresGrade>> allGrades = getAllGrades(jsonObject);
        List<SagresCalendarItem> calendar = getCalendar(jsonObject);
        List<SagresClassDetails> classDetails = getClassDetails(jsonObject);

        SagresProfile profile = new SagresProfile(name, messages, classes, grades);
        profile.setAllSemestersGrades(allGrades);
        profile.setCalendar(calendar);
        profile.setScore(score);
        profile.setClassDetails(classDetails);
        return profile;
    }

    private static List<SagresClassDetails> getClassDetails(JSONObject jsonObject) {
        List<SagresClassDetails> classDetails = new ArrayList<>();
        try {
            JSONArray classesArray = jsonObject.getJSONArray(CLASS_DETAILS_KEY);
            for (int i = 0; i < classesArray.length(); i++) {
                classDetails.add(SagresClassDetails.fromJSONObject(classesArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return classDetails;
    }

    private static HashMap<String, List<SagresClassDay>> getClasses(JSONObject jsonObject) throws JSONException {
        HashMap<String, List<SagresClassDay>> classes = new HashMap<>();

        try {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return classes;
    }

    private static List<SagresMessage> getMessages(JSONObject jsonObject) throws JSONException {
        List<SagresMessage> messages = new ArrayList<>();
        try {
            JSONArray messagesArray = jsonObject.optJSONArray(MESSAGES_KEY);
            for (int i = 0; i < messagesArray.length(); i++) {
                JSONObject messageObject = messagesArray.getJSONObject(i);
                SagresMessage message = SagresMessage.fromJSONObject(messageObject);
                messages.add(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return messages;
    }

    private static HashMap<String, SagresGrade> getGrades(JSONObject jsonObject) throws JSONException {
        List<SagresGrade> gradesList = new ArrayList<>();

        try {
            JSONArray jsonArray = jsonObject.optJSONArray(GRADES_KEY);
            for (int i = 0; i < jsonArray.length(); i++) {
                gradesList.add(SagresGrade.fromJSONObject(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        HashMap<String, SagresGrade> grades = new HashMap<>();
        for (SagresGrade grade : gradesList) {
            grades.put(grade.getClassCode(), grade);
        }
        return grades;
    }

    private static List<SagresGrade> getGradesWithoutTransformation(JSONArray jsonArray) throws JSONException {
        List<SagresGrade> gradesList = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                gradesList.add(SagresGrade.fromJSONObject(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return gradesList;
    }

    private static HashMap<SagresSemester,List<SagresGrade>> getAllGrades(JSONObject jsonObject) throws JSONException {
        HashMap<SagresSemester,List<SagresGrade>> allGrades = new HashMap<>();
        try {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return allGrades;
    }

    private static List<SagresCalendarItem> getCalendar(JSONObject jsonObject) throws JSONException {
        List<SagresCalendarItem> calendar = new ArrayList<>();

        try {
            JSONArray jsonArray = jsonObject.getJSONArray(CALENDAR_KEY);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                calendar.add(SagresCalendarItem.fromJSONObject(object));
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
        jsonObject.put(SCORE_KEY, score);

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
        
        JSONArray classDetails = classesDetailsToJSONArray();
        jsonObject.put(CLASS_DETAILS_KEY, classDetails);

        return jsonObject;
    }

    private JSONArray classesDetailsToJSONArray() throws JSONException {
        try {
            JSONArray jsonArray = new JSONArray();
            if (classDetails != null) {
                for (SagresClassDetails classDetail : classDetails) {
                    jsonArray.put(classDetail.toJSONObject());
                }
            }
            return jsonArray;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private JSONObject classesToJSONObject() throws JSONException {
        JSONObject classesObject = new JSONObject();

        if (classes != null) {
            for (String day : classes.keySet()) {
                JSONArray dayObject = new JSONArray();
                for (SagresClassDay classesDay : classes.get(day)) {
                    JSONObject classDayObject = classesDay.toJSONObject();
                    dayObject.put(classDayObject);
                }
                classesObject.put(day, dayObject);
            }
        }

        return classesObject;
    }

    private JSONArray messagesToJSONObject() throws JSONException {
        JSONArray messagesArray = new JSONArray();

        if (messages != null) {
            for (SagresMessage message : messages) {
                JSONObject messageObject = message.toJSONObject();
                messagesArray.put(messageObject);
            }
        }

        return messagesArray;
    }

    private JSONArray gradesToJSONArray() throws JSONException{
        JSONArray gradesArray = new JSONArray();

        if (grades != null) {
            for (String code : grades.keySet()) {
                SagresGrade grade = grades.get(code);
                JSONObject gradeObject = grade.toJSONObject();
                gradesArray.put(gradeObject);
            }
        }

        return gradesArray;
    }

    private JSONArray gradesToJSONArray(List<SagresGrade> grades) throws JSONException{
        JSONArray gradesArray = new JSONArray();

        if (grades != null) {
            for (SagresGrade grade : grades) {
                JSONObject gradeObject = grade.toJSONObject();
                gradesArray.put(gradeObject);
            }
        }

        return gradesArray;
    }

    private JSONArray allGradesToJSONArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();

        if (allSemestersGrades != null) {
            for (Map.Entry<SagresSemester, List<SagresGrade>> entry : allSemestersGrades.entrySet()) {
                JSONObject jsonObject = new JSONObject();
                SagresSemester key = entry.getKey();
                List<SagresGrade> grades = entry.getValue();
                jsonObject.put(SagresSemester.SEMESTER_NAME_KEY, key.getName());
                jsonObject.put(SagresSemester.SEMESTER_CODE_KEY, key.getSemesterCode());
                jsonObject.put(GRADES_KEY, gradesToJSONArray(grades));

                jsonArray.put(jsonObject);
            }
        }

        return jsonArray;
    }

    private JSONArray calendarToJSONArray() throws JSONException {
        JSONArray jsonArray = new JSONArray();

        if (calendar != null) {
            for (SagresCalendarItem item : calendar) {
                jsonArray.put(item.toJSONObject());
            }
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
        for(SagresMessage oldMessage : newMessages) {
            if (!newMessages.contains(oldMessage)) {
                newMessages.add(oldMessage);
            }
        }

        this.messages = newMessages;
        /*
        for (SagresMessage message : newMessages) {
            if (!messages.contains(message)) {
                messages.add(0, message);
            }
        }*/
    }

    public HashMap<String, SagresGrade> getGrades() {
        return grades;
    }

    public void placeNewGrades(HashMap<String, SagresGrade> grades) {
        this.grades = grades;

        if (allSemestersGrades != null) {
            List<SagresGrade> mergeList = new ArrayList<>();
            for (Map.Entry<String, SagresGrade> entry : grades.entrySet()) {
                mergeList.add(entry.getValue());
            }

            for (Map.Entry<SagresSemester, List<SagresGrade>> entry : allSemestersGrades.entrySet()) {
                if (entry.getValue().containsAll(mergeList)) {
                    allSemestersGrades.put(entry.getKey(), mergeList);
                    Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Changed grades of semester: " + entry.getKey().getName());
                    break;
                }
            }
        }

        setCurrentProfile(this);
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
        if (calendar == null)
            return;

        this.calendar = calendar;
        setCurrentProfile(this);
    }

    public List<SagresCalendarItem> getCalendar() {
        return calendar;
    }

    public void setScore(String score) {
        this.score = score;
        setCurrentProfile(this);
    }

    public String getScore() {
        return score;
    }

    public void setClasses(HashMap<String, List<SagresClassDay>> classes) {
        this.classes = classes;
    }

    public void setClassDetails(List<SagresClassDetails> classDetails) {
        this.classDetails = classDetails;
        setCurrentProfile(this);
    }

    public void updateClassDetails(List<SagresClassDetails> classDetailsUpdated) {
        if (classDetails == null) {
            classDetails = classDetailsUpdated;
            setCurrentProfile(this);
            return;
        }
        else if (classDetailsUpdated == null || classDetailsUpdated.isEmpty()) {
            System.out.println(classDetailsUpdated);
            setCurrentProfile(this);
            return;
        }

        for (SagresClassDetails updated : classDetailsUpdated) {
            SagresClassDetails correspondent = getCorrespondingClass(updated.getCode(), updated.getSemester());
            if (correspondent != null) {
                correspondent.setMissedClasses(updated.getMissedClasses());
                correspondent.setLastClass(updated.getLastClass());
                correspondent.setNextClass(updated.getNextClass());
                updateGroups(correspondent, updated);
            } else {
                Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Didn't exists before, but it's all ogre now: " + updated.getCode() + ": " + updated.getSemester());
                classDetails.add(updated);
            }
        }

        setCurrentProfile(this);
    }

    private void updateGroups(SagresClassDetails correspondent, SagresClassDetails updated) {
        if (correspondent.getGroups() == null) {
            correspondent.setGroups(updated.getGroups());
            return;
        }

        if (updated.getGroups() == null) {
            return;
        }

        if (correspondent.getGroups().isEmpty()) {
            correspondent.setGroups(updated.getGroups());
            return;
        }

        if(updated.getGroups().isEmpty()) {
            return;
        }

        if (correspondent.getGroups().size() == 1 && updated.getGroups().size() == 1) {
            SagresClassGroup upd = updated.getGroups().get(0);
            SagresClassGroup cor = correspondent.getGroups().get(0);

            if (upd.getTeacher() != null && !upd.getTeacher().trim().isEmpty()) {
                cor.updateFrom(upd);
            }

            return;
        }

        for (SagresClassGroup group : updated.getGroups()) {
            if (group.getTeacher() != null && !group.getTeacher().trim().isEmpty()) {
                SagresClassGroup corGroup = getCorrespondingGroup(correspondent, group.getType());
                if (corGroup == null) {
                    group.setDraft(false);
                    correspondent.addGroup(group);
                    Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "updateGroups: " + correspondent.getName() + " added group");
                } else {
                    if (corGroup.getTeacher() == null || group.getTeacher().trim().isEmpty()) {
                        Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "updateGroups: " + correspondent.getName() + " updated to new version");
                        corGroup.updateFrom(group);
                    }
                }
            }
        }

        for (SagresClassGroup oldGroups : correspondent.getGroups()) {
            if (getCorrespondingGroup(updated, oldGroups.getType()) == null) {
                Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Possible that user moved groups... check that later");
            }
        }
    }

    private SagresClassGroup getCorrespondingGroup(SagresClassDetails correspondent, String type) {
        for (SagresClassGroup group : correspondent.getGroups())
            if (type.equalsIgnoreCase(group.getType()))
                return group;

        return null;
    }

    private SagresClassDetails getCorrespondingClass(String code, String semester) {
        if (classDetails != null)
            for (SagresClassDetails classDetail : classDetails)
                if (classDetail.getSemester().equalsIgnoreCase(semester) && classDetail.getCode().equalsIgnoreCase(code))
                    return classDetail;

        return null;
    }

    public static void saveProfile() {
        SagresProfile.setCurrentProfile(SagresProfile.getCurrentProfile());
    }

    public synchronized void setGradesOfSemester(Map.Entry<SagresSemester,List<SagresGrade>> gradesOfSemester) {
        if (gradesOfSemester != null && gradesOfSemester.getValue() != null && !gradesOfSemester.getValue().isEmpty()) {
            if (allSemestersGrades == null)
                allSemestersGrades = new HashMap<>();

            if (allSemestersGrades.get(gradesOfSemester.getKey()) == null || allSemestersGrades.get(gradesOfSemester.getKey()).isEmpty()) {
                allSemestersGrades.put(gradesOfSemester.getKey(), gradesOfSemester.getValue());
            }
        }
    }

    public SagresClassDetails getClassDetailsWithParams(String code, String semester) {
        if (classDetails != null) {
            for (SagresClassDetails classGroup : classDetails) {
                if (classGroup.getCode().equalsIgnoreCase(code) && classGroup.getSemester().equalsIgnoreCase(semester)) {
                    return classGroup;
                }
            }
        }

        return null;
    }

    public SagresClassGroup getClassGroupWithParams(String code, String grouping, String semester) {
        if (classDetails != null)
            for (SagresClassDetails classGroup : classDetails)
                if (classGroup.getCode().equalsIgnoreCase(code) && classGroup.getSemester().equalsIgnoreCase(semester)) {
                    List<SagresClassGroup> groups = classGroup.getGroups();
                    if (grouping == null)
                        return groups.get(0);

                    for (SagresClassGroup group : groups) {
                        if (group.getType() == null && groups.size() == 1)
                            return group;

                        if (group.getType().contains(grouping))
                            return group;
                    }
                }

        return null;
    }

    public List<SagresClassDetails> getClassesDetails() {
        return classDetails;
    }
}
