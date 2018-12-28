package com.forcetower.uefs.sgrs.parsers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.crashlytics.android.Crashlytics;
import com.forcetower.uefs.db.entity.CourseVariant;
import com.forcetower.uefs.db.entity.Grade;
import com.forcetower.uefs.db.entity.GradeInfo;
import com.forcetower.uefs.db.entity.GradeSection;
import com.forcetower.uefs.util.WordUtils;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static com.forcetower.uefs.util.ValueUtils.toDouble;

/**
 * Created by João Paulo on 10/01/2018.
 * Upgraded version for v3.0.0 on 06/03/2018
 * - Better and faster parsing
 * - Ability to find the semester.
 *
 * Upgraded version for 6.2.0 on 30/06/2018
 * - Some courses have more than one variant... (I need to talk to someone about this)
 * - Selector can find the appropriate semester on parse time.
 */

public class SagresGradeParser {
    @Nullable
    public static String getPageSemester(Document document) {
        Element gradesDiv = document.selectFirst("div[id=\"divBoletins\"]");
        if (gradesDiv == null) {
            Crashlytics.logException(new Exception("Grades Div is null"));
            Timber.d("Grades Div is null");
            return null;
        }

        Elements classes = gradesDiv.select("div[class=\"boletim-container\"]");
        if (classes == null) {
            Crashlytics.logException(new Exception("Container Div is null"));
            Timber.d("Container Div is null");
            return null;
        }

        Elements semestersValues = document.select("option[selected=\"selected\"]");
        if (semestersValues.size() == 1) {
            return semestersValues.get(0).text();
        } else if (semestersValues.size() > 1) {
            Timber.d("Selected values size is greater than 1");
            Element select = document.selectFirst("select[id=\"ctl00_MasterPlaceHolder_ddPeriodosLetivos_ddPeriodosLetivos\"]");
            if (select != null) {
                Element selected = select.selectFirst("option[selected=\"selected\"]");
                if (selected != null) {
                    Timber.d("Second Select working");
                    return selected.text();
                } else {
                    Crashlytics.logException(new Exception("Even after second select it is invalid"));
                    Timber.d("Select option is null after second Select");
                }
            } else {
                Crashlytics.logException(new Exception("The hole is lower. Cant find second select"));
                Timber.d("Cant find select for this one query");
            }
        } else {
            Timber.d("There are no values selected... How fun");
            StringBuilder sb = new StringBuilder();
            for (Element element : semestersValues) {
                sb.append(element.text()).append(" <> ");
            }
            String elements = sb.toString();
            Crashlytics.logException(new Exception("The number of values selected in the spinner is " + semestersValues.size() + ":: Elements: " + elements));
        }

        return null;
    }

    public static Long getSelectedSemester(Document document) {
        Element element = document.selectFirst("select[id=\"ctl00_MasterPlaceHolder_ddPeriodosLetivos_ddPeriodosLetivos\"]");
        if (element != null) {
            Element selected = element.selectFirst("option[selected=\"selected\"]");
            if (selected != null) {
                String value = selected.attr("value").trim();
                try {
                    return Long.parseLong(value);
                } catch (Throwable ignored) {}
            }
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
        try {
            Element gradesDiv = document.selectFirst("div[id=\"divBoletins\"]");
            Elements classes = gradesDiv.select("div[class=\"boletim-container\"]");

            for (Element element : classes) {
                try {
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
                } catch (Exception e) {
                    Crashlytics.logException(e);
                }
            }
        } catch (Throwable ignored) {}

        return grades;
    }

    public static List<CourseVariant> findVariants(@NonNull Document document) {
        List<CourseVariant> variants = new ArrayList<>();

        Elements semestersValues = document.select("option[selected=\"selected\"]");
        if (semestersValues.size() > 1) {
            Timber.d("There is more than one variant");
            Element variant = document.selectFirst("select[id=\"ctl00_MasterPlaceHolder_ddRegistroCurso\"]");
            if (variant == null) {
                Timber.d("More than 1 selected but variant spinner is not found");
                Crashlytics.logException(new Exception("More than 1 selected but variant spinner is not found"));
            } else {
                Elements options = variant.children();
                Timber.d("Number of variants: " + options.size());
                for (Element option : options) {
                    String uefsId = option.attr("value");
                    String name = WordUtils.toTitleCase(option.text().trim());
                    variants.add(new CourseVariant(uefsId, name));
                    Timber.d("Variant: " + uefsId + " -> " + name);
                }
            }
        } else {
            Timber.d("No variants found! App will behave normally");
        }
        return variants;
    }
}
