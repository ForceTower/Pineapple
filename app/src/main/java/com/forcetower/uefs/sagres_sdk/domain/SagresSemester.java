package com.forcetower.uefs.sagres_sdk.domain;

/**
 * Created by João Paulo on 28/11/2017.
 */

public class SagresSemester {
    public static final String SEMESTER_CODE_KEY = "semester_code";
    public static final String SEMESTER_NAME_KEY = "semester_name";

    private String semesterCode;
    private String name;

    public SagresSemester(String semesterCode, String name) {
        this.semesterCode = semesterCode;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSemesterCode() {
        return semesterCode;
    }

    @Override
    public int hashCode() {
        return semesterCode.hashCode();
    }
}
