package com.forcetower.uefs;

import android.app.Application;

import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.model.UClass;
import com.forcetower.uefs.model.UClassDay;

import java.util.HashMap;
import java.util.List;

/**
 * Created by João Paulo on 09/11/2017.
 */

public class UEFSApplication extends Application {
    private String html;
    private HashMap<String, UClass> classes;
    private HashMap<String, List<UClassDay>> schedule;

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
        schedule = Utils.getSchedule(classes);
    }

    public HashMap<String, UClass> getClasses() {
        return classes;
    }

    public HashMap<String, List<UClassDay>> getSchedule() {
        return schedule;
    }
}
