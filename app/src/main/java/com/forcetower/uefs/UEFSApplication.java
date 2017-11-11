package com.forcetower.uefs;

import android.app.Application;

import com.forcetower.uefs.model.UClass;

import java.util.HashMap;

/**
 * Created by João Paulo on 09/11/2017.
 */

public class UEFSApplication extends Application {
    private String html;
    private HashMap<String, UClass> classes;

    public UEFSApplication() {
        super();
    }

    public void saveHtml(String html) {
        this.html = html;
    }

    public String getHtml() {
        return html;
    }

    public void saveClasses(HashMap<String, UClass> classes) {
        this.classes = classes;
    }

    public HashMap<String, UClass> getClasses() {
        return classes;
    }
}
