package com.forcetower.uefs.ru;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Calendar;
import java.util.List;

@IgnoreExtraProperties
@SuppressWarnings("SpellCheckingInspection")
public class RUData {
    private String time;
    private boolean aberto;
    private String cotas;
    private boolean error;
    private long currentTime;
    private String mealType;

    public RUData() {}

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isAberto() {
        return aberto;
    }

    public void setAberto(boolean aberto) {
        this.aberto = aberto;
    }

    public String getCotas() {
        return cotas;
    }

    public void setCotas(String cotas) {
        this.cotas = cotas;
        this.cotas = cotas.replaceAll("[^0-9]", "");
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public Calendar getCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        return calendar;
    }

    public boolean isError() {
        return error;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
        this.mealType = mealType.trim();
    }

    public String getMealType() {
        return mealType;
    }
}
