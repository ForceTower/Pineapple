package com.forcetower.uefs.db.entity;

import androidx.room.Ignore;

import java.util.List;

public class CreditsMention {
    private int uid;
    private String category;
    @Ignore
    private List<Mention> participants;

    public CreditsMention(String category, List<Mention> participants) {
        this.category = category;
        this.participants = participants;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<Mention> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Mention> participants) {
        this.participants = participants;
    }
}
