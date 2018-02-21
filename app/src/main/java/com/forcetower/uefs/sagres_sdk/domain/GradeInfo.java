package com.forcetower.uefs.sagres_sdk.domain;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import static com.forcetower.uefs.Constants.APP_TAG;
import static com.forcetower.uefs.helpers.Utils.parseIntOrZero;

/**
 * Created by João Paulo on 25/11/2017.
 */
public class GradeInfo {
    private static final String EVALUATION_NAME_KEY = "evaluation_name";
    private static final String GRADE_KEY = "grade";
    private static final String DATE_KEY = "date";
    private static final String WEIGHT_KEY = "weight";

    private String evalName;
    private String grade;
    private String date;
    private String weight;

    public GradeInfo(String evalName, String grade, String date) {
        if (grade.trim().isEmpty())
            grade = "0,0";

        this.evalName = evalName;
        this.grade = grade;
        this.date = date;
    }

    public GradeInfo(String evalName, String grade, String date, String weight) {
        this(evalName, grade, date);
        this.weight = weight;
    }

    public String getEvaluationName() {
        return evalName;
    }

    public String getGrade() {
        return grade;
    }

    public String getDate() {
        return date;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(EVALUATION_NAME_KEY, evalName);
        jsonObject.put(GRADE_KEY, grade);
        jsonObject.put(DATE_KEY, date);
        jsonObject.put(WEIGHT_KEY, weight);
        return jsonObject;
    }

    public static GradeInfo fromJSONObject(JSONObject jsonObject) throws JSONException {
        String evalName = jsonObject.optString(EVALUATION_NAME_KEY, "Sem nome");
        String grade = jsonObject.optString(GRADE_KEY, "--");
        String date = jsonObject.optString(DATE_KEY, "Sem data");
        String weight = jsonObject.optString(WEIGHT_KEY, null);

        return new GradeInfo(evalName, grade, date, weight);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GradeInfo gradeInfo = (GradeInfo) o;

        if (evalName != null ? !evalName.equals(gradeInfo.evalName) : gradeInfo.evalName != null)
            return false;
        if (grade != null ? !grade.equals(gradeInfo.grade) : gradeInfo.grade != null) return false;
        //return date != null ? date.equals(gradeInfo.date) : gradeInfo.date == null;
        return true;
    }

    @Override
    public int hashCode() {
        int result = evalName != null ? evalName.hashCode() : 0;
        result = 31 * result + (grade != null ? grade.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public double getCalculatedValue() {
        if (getWeight() != null && getGrade() != null) {
            if (getGrade().equalsIgnoreCase("Não Divulgada")) {
                return -1;
            }

            String grade = getGrade();
            grade = grade.replace(",", ".");
            String weight = getWeight();
            weight = weight.replace(",", ".");
            if (grade.endsWith("*")) grade = grade.substring(0, grade.length() - 1);
            try {
                return Double.parseDouble(grade) * Double.parseDouble(weight);
            } catch (Exception e) {
                Log.e(APP_TAG, "unable to parse double: " + grade + " or " + weight);
                return -1;
            }
        }
        return -1;
    }

    public double getWeightValue() {
        String weight = getWeight();
        weight = weight.replace(",", ".");
        try {
            return Double.parseDouble(weight);
        } catch (Exception e) {
            Log.e(APP_TAG, "unable to parse double: " + weight);
            return -1;
        }
    }

    public String getWeight() {
        return weight;
    }
}
