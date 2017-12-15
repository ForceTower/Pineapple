package com.forcetower.uefs.sagres_sdk.parsers;

import android.util.Log;

import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.GradeInfo;
import com.forcetower.uefs.sagres_sdk.domain.GradeSection;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;
import com.forcetower.uefs.sagres_sdk.domain.SagresSemester;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by João Paulo on 25/11/2017.
 */

public class SagresGradesParser {

    public static HashMap<SagresSemester, List<SagresGrade>> getAllGrades(String html, String semester) {
        Document htmlDocument = Jsoup.parse(html);
        htmlDocument.charset(Charset.forName("UTF-8"));

        HashMap<SagresSemester, List<SagresGrade>> semesterGrades = new HashMap<>();

        Elements semestersValues = htmlDocument.select("option");

        for (Element element : semestersValues) {
            SagresPortalSDK.alertConnectionListeners(3, element.text());
            Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Value: " + element.attr("value"));
            Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Semester: " + element.text());

            if (semester == null || semester.equalsIgnoreCase(element.text())) {
                List<SagresGrade> grades = getGradesFor(element.attr("value"), htmlDocument);
                if (grades != null && !grades.isEmpty()) {
                    semesterGrades.put(new SagresSemester(element.attr("value"), element.text()), grades);
                } else {
                    semesterGrades.put(new SagresSemester(element.attr("value"), element.text()), new ArrayList<SagresGrade>());
                }
            }
        }

        return semesterGrades;
    }

    private static List<SagresGrade> getGradesFor(String semesterVal, Document html) {
        FormBody.Builder formBody = new FormBody.Builder();

        Elements elements = html.select("input[value][type=\"hidden\"]");

        for (Element element : elements) {
            String key = element.attr("id");
            String value = element.attr("value");
            formBody.add(key, value);
        }

        formBody.add("ctl00$MasterPlaceHolder$ddPeriodosLetivos$ddPeriodosLetivos", semesterVal);
        formBody.add("ctl00$MasterPlaceHolder$imRecuperar", "Exibir");

        Request request = new Request.Builder()
                .url("http://academico2.uefs.br/Portal/Modules/Diario/Aluno/Relatorio/Boletim.aspx?op=notas")
                .post(formBody.build())
                .addHeader("x-requested-with", "XMLHttpRequest")
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();

        try {
            Response response = SagresPortalSDK.getHttpClient().newCall(request).execute();
            String htmlSemester = response.body().string();
            return extractGrades(htmlSemester);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<SagresGrade> extractGrades(String htmlSemester) {
        List<SagresGrade> grades = new ArrayList<>();

        Document html = Jsoup.parse(htmlSemester);
        Element gradesDiv = html.selectFirst("div[id=\"divBoletins\"]");

        Elements classes = gradesDiv.select("div[class=\"boletim-container\"]");
        for (Element element : classes) {
            Element classInfo = element.selectFirst("div[class=\"boletim-item-info\"]");
            Element className = classInfo.selectFirst("span[class=\"boletim-item-titulo cor-destaque\"]");
            SagresGrade clazz = new SagresGrade(className.text());

            Element situation = classInfo.selectFirst("span[class=\"boletim-item-resumo\"]");
            if (situation != null) {
                //System.out.println("Situation: " + situation.text());
            } else {
                //System.out.println("Situation: Not released yet");
            }

            Element gradesInfo = element.selectFirst("div[class=\"boletim-notas\"]");
            Element gradesTable = gradesInfo.selectFirst("table");
            Element gradesTBody = gradesTable.selectFirst("tbody");
            if (gradesTBody != null) {
                Elements trs = gradesTBody.select("tr");
                if (!trs.isEmpty()) {
                    GradeSection section = new GradeSection("Não definido");

                    for (Element tr : trs) {
                        Elements children = tr.children();
                        if (children.size() == 2) {
                            //section.addGradeInfo(new GradeInfo("Prova 987", "10,7", "31/12/2017"));
                            if (section.getGrades().size() > 0) {
                                clazz.addSection(section);
                            }

                            section = new GradeSection(children.get(1).text());
                        } else if (children.size() == 4) {
                            Element td = children.first();
                            if (td.children().size() == 0) {
                                //System.out.println("Final!");
                                Element meanTests = children.get(2);
                                //System.out.println("Partial Mean " + meanTests.text());
                            } else {
                                Element date = children.get(0);
                                Element identification = children.get(1);
                                Element grade = children.get(2);
                                GradeInfo gradeInfo = new GradeInfo(identification.text(), grade.text(), date.text());
                                section.addGradeInfo(gradeInfo);
                            }
                        }
                    }
                }
            }

            Element tFoot = gradesTable.selectFirst("tfoot");
            if (tFoot != null) {
                Element tr = tFoot.selectFirst("tr");
                if (tr != null && tr.children().size() == 4) {
                    clazz.setFinalScore(tr.children().get(2).text());
                }
            }

            grades.add(clazz);
        }

        return grades;
    }
}
