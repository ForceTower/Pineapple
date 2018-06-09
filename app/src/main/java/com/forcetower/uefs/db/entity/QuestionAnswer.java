package com.forcetower.uefs.db.entity;

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by João Paulo on 18/04/2018.
 */
public class QuestionAnswer {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String question;
    private String answer;

    @Ignore
    public QuestionAnswer(int uid, String question, String answer) {
        this.uid = uid;
        this.question = question;
        this.answer = answer;
    }

    public QuestionAnswer(String question, String answer) {
        this.question = question;
        this.answer = answer;
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
}
