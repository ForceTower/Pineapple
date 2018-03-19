package com.forcetower.uefs.sgrs.parsers;

import com.forcetower.uefs.db.entity.DisciplineClassItem;
import com.forcetower.uefs.db.entity.DisciplineGroup;
import com.forcetower.uefs.util.WordUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.forcetower.uefs.util.ValueUtils.toInteger;

/**
 * Created by João Paulo on 09/03/2018.
 */

public class SagresDisciplineDetailsParser {

    public static DisciplineGroup parseDisciplineGroup(Document document) {
        Element elementName = document.selectFirst("h2[class=\"cabecalho-titulo\"]");
        if (elementName == null) {
            return null;
        }

        String classNameFull = elementName.text();

        int codePos = classNameFull.indexOf("-");
        String code = classNameFull.substring(0, codePos).trim();
        int groupPos = classNameFull.lastIndexOf("(");
        String group = classNameFull.substring(groupPos);
        int refGroupPos = group.lastIndexOf("-");
        String refGroup = group.substring(refGroupPos + 1, group.length() - 1).trim();
        String name = classNameFull.substring(codePos + 1, groupPos).trim();
        String teacher = "";
        Element elementTeacher = document.selectFirst("div[class=\"cabecalho-dado nome-capitalizars\"]");
        if (elementTeacher != null) {
            elementTeacher = elementTeacher.selectFirst("span");
            if (elementTeacher != null) teacher = WordUtils.toTitleCase(elementTeacher.text());
        }

        DisciplineGroup created = new DisciplineGroup(0, teacher, refGroup, 0, 0,null, null);

        String semesterByName = null;
        String classCredits = "0";
        String missLimits = "0";
        String classPeriod = null;
        String department = null;

        for (Element element : document.select("div[class=\"cabecalho-dado\"]")) {
            Element b = element.child(0);
            String bText = b.text();
            if (bText.equalsIgnoreCase("Período:")) {
                semesterByName = element.child(1).text();
            }

            else if (bText.equalsIgnoreCase("Carga horária:") && classCredits.isEmpty()) {
                classCredits = element.child(1).text();
                classCredits = classCredits.replaceAll("[^\\d]", "").trim();
            }

            else if (bText.equalsIgnoreCase("Limite de Faltas:")) {
                missLimits = element.child(1).text();
                missLimits = missLimits.replaceAll("[^\\d]", "").trim();
            }

            else if (bText.equalsIgnoreCase("Período de aulas:")) {
                classPeriod = element.selectFirst("span").text();
            }

            else if (bText.equalsIgnoreCase("Departamento:")) {
                department = WordUtils.toTitleCase(element.child(1).text());
            }

            else if (bText.equalsIgnoreCase("Horário:")) {
                for (Element classTime : element.select("div[class=\"cabecalho-horario\"]")) {
                    String day = classTime.child(0).text();
                    String start = classTime.child(1).text();
                    String end = classTime.child(3).text();
                }
            }
        }

        created.setDepartment(department);
        created.setClassPeriod(classPeriod);
        created.setDisciplineCodeAndSemester(code, semesterByName);
        try {
            int credits = Integer.parseInt(classCredits);
            created.setCredits(credits);
            int maxMiss = Integer.parseInt(missLimits);
            created.setMissLimit(maxMiss);
        } catch (Exception e) {
            Timber.d("Exception in parse int for numbers");
        }

        return created;
    }

    public static List<DisciplineClassItem> parseDisciplineClassItems(Document document) {
        List<DisciplineClassItem> items = new ArrayList<>();

        Elements trs = document.select("tr[class]");
        for (int i = 1; i < trs.size(); i++) {
            Element tr = trs.get(i);
            Elements tds = tr.select("td");
            if (!tds.isEmpty()) {
                DisciplineClassItem classItem = getFromTDs(tds);
                if (classItem != null) items.add(classItem);
            }
        }

        return items;
    }

    private static DisciplineClassItem getFromTDs(Elements tds) {
        try {
            String strNumber = tds.get(0).text();
            String situation = tds.get(1).text();
            String date = tds.get(2).text();
            String description = tds.get(3).text();
            String strMaterials = tds.get(5).text();
            int number = toInteger(strNumber, -1);
            int materials = toInteger(strMaterials, -1);
            return new DisciplineClassItem(0, number, situation, description, date, materials);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
