package com.forcetower.uefs.database.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by João Paulo on 29/12/2017.
 */
@Entity
public class AScrap {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String sender;
    private String message;
    @ColumnInfo(name = "received_at")
    private String receivedAt;
    @ColumnInfo(name = "class_received")
    private String classReceived;

    public AScrap(String sender, String message, String receivedAt, String classReceived) {
        this.sender = sender;
        this.message = message;
        this.receivedAt = receivedAt;
        this.classReceived = classReceived;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }

    public String getClassReceived() {
        return classReceived;
    }

    public void setClassReceived(String classReceived) {
        this.classReceived = classReceived;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }
}
