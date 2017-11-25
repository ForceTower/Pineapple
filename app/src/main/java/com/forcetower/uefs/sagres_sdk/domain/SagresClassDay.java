package com.forcetower.uefs.sagres_sdk.domain;

import android.support.annotation.NonNull;

import com.forcetower.uefs.sagres_sdk.utility.SagresDayUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by João Paulo on 17/11/2017.
 */

public class SagresClassDay implements Comparable<SagresClassDay> {
    //Class Keys
    private static final String CLASS_CODE_KEY = "code";
    private static final String ROOM_KEY = "room";
    private static final String START_KEY = "starts_at";
    private static final String END_KEY = "ends_at";
    private static final String CAMPUS_KEY = "campus";
    private static final String DAY_KEY = "day";
    private static final String CLASS_TYPE_KEY = "class_type";
    private static final String MODULO_KEY = "modulo";
    private static final String CLASS_NAME_KEY = "class_name";

    //Attributes
    private String starts_at;
    private String ends_at;
    private String class_type;
    private String day;
    private String room;
    private String campus;
    private String modulo;
    private String class_code;
    private String class_name;
    private long duration;

    private Calendar starts;
    private Calendar ends;

    public SagresClassDay(String class_code, String class_name, String starts_at, String ends_at, String class_type, String day, String room, String campus, String modulo) {
        this.starts_at = starts_at;
        this.ends_at = ends_at;
        this.class_type = class_type;
        this.day = day;
        this.room = room;
        this.campus = campus;
        this.modulo = modulo;
        this.class_code = class_code;
        this.class_name = class_name;

        this.starts = SagresDayUtils.generateCalendar(starts_at);
        this.ends = SagresDayUtils.generateCalendar(ends_at);
        this.duration = SagresDayUtils.getDateDiff(starts.getTime(), ends.getTime(), TimeUnit.HOURS);
    }

    public SagresClassDay(String start, String finish, String day, String classType, SagresClass sagresClass) {
        this(sagresClass.getCode(), sagresClass.getName(), start, finish, classType, day, null, null, null);
    }

    public static SagresClassDay fromJSONObject(JSONObject jsonObject) throws JSONException {
        String starts_at = jsonObject.getString(START_KEY);
        String ends_at = jsonObject.getString(END_KEY);
        String class_type = jsonObject.getString(CLASS_TYPE_KEY);
        String day = jsonObject.getString(DAY_KEY);
        String room = jsonObject.getString(ROOM_KEY);
        String campus = jsonObject.getString(CAMPUS_KEY);
        String modulo = jsonObject.getString(MODULO_KEY);
        String class_code = jsonObject.getString(CLASS_CODE_KEY);
        String class_name = jsonObject.getString(CLASS_NAME_KEY);

        return new SagresClassDay(class_code, class_name, starts_at, ends_at, class_type, day, room, campus, modulo);
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(START_KEY, starts_at);
        jsonObject.put(END_KEY, ends_at);
        jsonObject.put(CLASS_TYPE_KEY, class_type);
        jsonObject.put(DAY_KEY, day);
        jsonObject.put(ROOM_KEY, room);
        jsonObject.put(CAMPUS_KEY, campus);
        jsonObject.put(MODULO_KEY, modulo);
        jsonObject.put(CLASS_CODE_KEY, class_code);
        jsonObject.put(CLASS_NAME_KEY, class_name);
        return jsonObject;
    }

    public String getDay() {
        return day;
    }

    public Calendar getStartsAt() {
        return starts;
    }

    public long getDuration() {
        return duration;
    }

    public String getStartString() {
        return starts_at;
    }

    public String getFinishString() {
        return ends_at;
    }

    public String getClassType() {
        return class_type;
    }

    public String getAllocatedRoom() {
        return room;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Calendar getStart() {
        return starts;
    }

    public Calendar getFinish() {
        return ends;
    }

    public String getClassName() {
        return class_name;
    }

    public void setClassName(String className) {
        this.class_name = className;
    }

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public void setClassCode(String classCode) {
        this.class_code = classCode;
    }

    public int compareTo(@NonNull SagresClassDay other) {
        if (other.getDay().equalsIgnoreCase(this.getDay())) {
            if (starts.before(other.getStartsAt())) {
                return -1;
            } else {
                return 1;
            }
        } else {
            return SagresDayUtils.compareDayOfWeek(day, other.getDay());
        }
    }
}
