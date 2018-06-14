package com.forcetower.uefs.sgrs.parsers;

import androidx.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.db.entity.Grade;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.GradeSection;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.forcetower.uefs.util.ValueUtils.toDouble;

/**
 * Created by João Paulo on 06/03/2018.
 */

public class SagresGradeParser {
    @Nullable
    public static String getPageSemester(Document document) {
        Element gradesDiv = document.selectFirst("div[id=\"divBoletins\"]");
        if (gradesDiv == null) {
            Crashlytics.logException(new Exception("Grades Div is null"));
            return null;
        }

        Elements classes = gradesDiv.select("div[class=\"boletim-container\"]");
        if (classes == null) {
            Crashlytics.logException(new Exception("Container Div is null"));
            return null;
        }

        Elements semestersValues = document.select("option[selected=\"selected\"]");
        if (semestersValues.size() == 1) {
            return semestersValues.get(0).text();
        } else {
            Timber.d("There are 2 values selected... How fun");
            Crashlytics.logException(new Exception("The number of values selected in the spinner is " + semestersValues.size()));
        }

        return null;
    }

    /**
     * This method assumes that the page is functional. if the page is wrong a NullPointerException
     * will be thrown
     * @param document document to parse
     */
    public static List<Grade> getGrades(Document document) {
        List<Grade> grades = new ArrayList<>();
        Element gradesDiv = document.selectFirst("div[id=\"divBoletins\"]");
        Elements classes = gradesDiv.select("div[class=\"boletim-container\"]");

        for (Element element : classes) {
            Element classInfo = element.selectFirst("div[class=\"boletim-item-info\"]");
            Element className = classInfo.selectFirst("span[class=\"boletim-item-titulo cor-destaque\"]");

            String discipline = className.text();
            Grade clazz = new Grade(discipline);

            Element gradesInfo = element.selectFirst("div[class=\"boletim-notas\"]");
            Element gradesTable = gradesInfo.selectFirst("table");
            Element gradesTBody = gradesTable.selectFirst("tbody");


            if (gradesTBody != null) {
                Elements trs = gradesTBody.select("tr");
                if (!trs.isEmpty()) {
                    GradeSection section = new GradeSection(discipline, "Não Definido");
                    for (Element tr : trs) {
                        Elements children = tr.children();
                        if (children.size() == 2) {
                            if (section.getGrades().size() > 0) {
                                clazz.addSection(section);
                            }

                            section = new GradeSection(children.get(1).text(), discipline);
                        } else if (children.size() == 4) {
                            Element td = children.first();
                            if (td.children().size() == 0) {
                                Element meanTests = children.get(2);
                                clazz.setPartialMean(meanTests.text());
                            } else {
                                Element date = children.get(0);
                                Element identification = children.get(1);
                                Element grade = children.get(2);
                                Element weight = children.get(3);
                                GradeInfo gradeInfo = new GradeInfo(identification.text(), grade.text(), date.text());
                                gradeInfo.setWeight(toDouble(weight.text(), 0));
                                section.addGrade(gradeInfo);
                            }
                        } else if (children.size() == 3) {
                            section.setPartialMean(children.get(1).text());
                            Timber.d("section partial %s", section.getPartialMean());
                        }
                    }

                    if (section.getGrades().size() != 0) {
                        Timber.d("Almost missed you %s", section.getName());
                        Timber.d("%s", section.getGrades());
                        clazz.addSection(section);
                    }
                }
            } else {
                Timber.d("Grades tbody is null... Just wondering");
                Crashlytics.log("Grades tbody is null, this will make this user angry");
            }

            Element tFoot = gradesTable.selectFirst("tfoot");
            if (tFoot != null) {
                Element tr = tFoot.selectFirst("tr");
                if (tr != null && tr.children().size() == 4) {
                    clazz.setFinalScore(tr.children().get(2).text());
                }
            }
            Timber.d("Grade clazz: %s", clazz);
            grades.add(clazz);
        }

        return grades;
    }
}
