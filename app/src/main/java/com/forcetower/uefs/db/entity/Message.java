package com.forcetower.uefs.db.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.SpannableString;

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

    @Ignore
    @Nullable
    private SpannableString spannable;

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
                    int r1 =  date1.compareTo(date2) *-1;
                    if (r1 == 0) r1 = Integer.compare(getUid(), o.getUid()) * -1;
                    return r1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                String tts = getReceivedAt().replaceAll("[^\\d]", "").trim();
                String oos = o.getReceivedAt().replaceAll("[^\\d]", "").trim();
                int tth = Integer.parseInt(tts) + 1;
                int ooh = Integer.parseInt(oos) + 1;

                if (getReceivedAt().contains("dia"))
                    tth *= 100;
                if (o.getReceivedAt().contains("dia"))
                    ooh *= 100;

                int r1 = Integer.compare(tth, ooh);
                if (r1 == 0) r1 = Integer.compare(getUid(), o.getUid())*-1;
                return r1;
            }
        }

        return Integer.compare(o.getUid(), getUid());
    }

    public void setSpannable(@Nullable SpannableString spannable) {
        this.spannable = spannable;
    }

    @Nullable
    public SpannableString getSpannable() {
        return spannable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (uid != message1.uid) return false;
        if (sender != null ? !sender.equals(message1.sender) : message1.sender != null)
            return false;
        if (message != null ? !message.equals(message1.message) : message1.message != null)
            return false;
        if (receivedAt != null ? !receivedAt.equals(message1.receivedAt) : message1.receivedAt != null)
            return false;
        return classReceived != null ? classReceived.equals(message1.classReceived) : message1.classReceived == null;
    }

    @Override
    public int hashCode() {
        int result = uid;
        result = 31 * result + (sender != null ? sender.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (receivedAt != null ? receivedAt.hashCode() : 0);
        result = 31 * result + (classReceived != null ? classReceived.hashCode() : 0);
        return result;
    }
}
