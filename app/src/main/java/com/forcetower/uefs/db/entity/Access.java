package com.forcetower.uefs.db.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Entity
public class Access {
    @PrimaryKey(autoGenerate = true)
    private int uid;
    private String username;
    private String password;

    public Access(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public String getUsernameFixed() {
        if (username == null) return "";

        return username.replace(".", "")
                .replace("#", "")
                .replace("$", "")
                .replace("[", "")
                .replace("]", "");
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void copyFrom(Access access) {
        setUsername(access.getUsername());
        setPassword(access.getPassword());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Access) {
            Access a = (Access) obj;
            return a.username.equals(username) && a.password.equals(password);
        }
        return false;
    }
}
