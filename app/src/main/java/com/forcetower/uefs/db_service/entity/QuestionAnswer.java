package com.forcetower.uefs.db_service.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by João Paulo on 18/04/2018.
 */
@Entity
public class QuestionAnswer {
    @PrimaryKey(autoGenerate = true)
    @SerializedName(value = "id")
    private int uid;
    private String question;
    private String answer;
    private boolean active;

    @Ignore
    public QuestionAnswer(int uid, String question, String answer) {
        this.uid = uid;
        this.question = question;
        this.answer = answer;
        this.active = true;
    }

    public QuestionAnswer(String question, String answer, boolean active) {
        this.question = question;
        this.answer = answer;
        this.active = active;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof QuestionAnswer) {
            QuestionAnswer o = (QuestionAnswer) obj;
            return o.uid == uid;
        }
        return false;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
