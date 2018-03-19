package com.forcetower.uefs.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.forcetower.uefs.util.WordUtils.validString;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Entity
public class Message implements Comparable<Message>{
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String sender;
    private String message;
    @ColumnInfo(name = "received_at")
    private String receivedAt;
    @ColumnInfo(name = "class_received")
    private String classReceived;
    private int notified;

    public Message(String sender, String message, String receivedAt, String classReceived) {
        this.sender = sender;
        this.message = message;
        this.receivedAt = receivedAt;
        this.classReceived = classReceived;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public int getNotified() {
        return notified;
    }

    public void setNotified(int notified) {
        this.notified = notified;
    }

    public void selectiveCopy(Message other) {
        if (validString(other.getReceivedAt()))
            this.setReceivedAt(other.getReceivedAt());
    }

    @Override
    public int compareTo(@NonNull Message o) {
        if (getReceivedAt().contains("/") && !o.getReceivedAt().contains("/"))
            return 1;
        else if (!getReceivedAt().contains("/") && o.getReceivedAt().contains("/"))
            return -1;
        else {
            if (getReceivedAt().contains("/") && o.getReceivedAt().contains("/")) {
                String tS = getReceivedAt();
                String oS = o.getReceivedAt();

                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

                Date date1;
                Date date2;
                try {
                    date1 = format.parse(tS);
                    date2 = format.parse(oS);
                    return date1.compareTo(date2) *-1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                if (getReceivedAt().contains("hora") && o.getReceivedAt().contains("hora")) {
                    String thS = getReceivedAt().replaceAll("[^\\d]", "").trim();
                    String ohS = o.getReceivedAt().replaceAll("[^\\d]", "").trim();
                    int th = Integer.parseInt(thS);
                    int oh = Integer.parseInt(ohS);
                    return Integer.compare(th, oh);
                } else if (!getReceivedAt().contains("hora") && !o.getReceivedAt().contains("hora")){
                    String thS = getReceivedAt().replaceAll("[^\\d]", "").trim();
                    String ohS = o.getReceivedAt().replaceAll("[^\\d]", "").trim();
                    int th = Integer.parseInt(thS);
                    int oh = Integer.parseInt(ohS);
                    return Integer.compare(th, oh) * -1;
                } else if (!getReceivedAt().contains("hora")){
                    return -1;
                } else {
                    return 1;
                }
            }
        }

        return Integer.compare(o.getUid(), getUid());
    }
}
