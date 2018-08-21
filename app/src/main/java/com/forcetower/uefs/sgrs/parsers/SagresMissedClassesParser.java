package com.forcetower.uefs.sgrs.parsers;

import androidx.annotation.NonNull;
import android.util.Pair;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.db.entity.DisciplineMissedClass;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by João Paulo on 23/06/2018.
 */
public class SagresMissedClassesParser {

    public static Pair<Boolean, List<DisciplineMissedClass>> getMissedClasses(@NonNull Document document) {
        List<DisciplineMissedClass> list = new ArrayList<>();
        boolean error = false;

        try {
            Element gradesDiv = document.selectFirst("div[id=\"divBoletins\"]");
            Elements classes = gradesDiv.select("div[class=\"boletim-container\"]");

            for (Element klass : classes) {
                Element classInfo = klass.selectFirst("div[class=\"boletim-item-info\"]");
                Element className = classInfo.selectFirst("span[class=\"boletim-item-titulo cor-destaque\"]");

                String klassText = className.text();
                String code = klassText.substring(0, klassText.indexOf("-") - 1).trim();
                Timber.d("Class Code: " + code);

                Element frequency = klass.selectFirst("div[class=\"boletim-frequencia\"]");
                Element frequencyTable = frequency.selectFirst("table");
                if (frequencyTable == null) {
                    Timber.d("No misses for this one - No Table");
                } else {
                    Element frequencyBody = frequencyTable.selectFirst("tbody");
                    if (frequencyBody == null) {
                        Timber.d("No misses for this one - No Body");
                    } else {
                        List<DisciplineMissedClass> parsed = fourierTransform(frequencyBody, code);
                        list.addAll(parsed);
                    }
                }
            }

        } catch (Throwable t) {
            //Nothing shall exit here crashing the app
            t.printStackTrace();
            Crashlytics.logException(t);
            error = true;
        }

        return new Pair<>(error, list);
    }

    private static List<DisciplineMissedClass> fourierTransform(Element body, String code) {
        List<DisciplineMissedClass> list = new ArrayList<>();

        Elements trs = body.select("tr");
        for (Element tr : trs) {
            Elements elements = tr.child(0).child(0).children();
            String date = elements.get(0).text().trim();
            String description = elements.get(1).text().trim();

            Timber.d("Date: " + date);
            Timber.d("Desc: " + description);
            DisciplineMissedClass miss = new DisciplineMissedClass(date, description);
            miss.setDisciplineCode(code);
            list.add(miss);
        }
        return list;
    }
}
