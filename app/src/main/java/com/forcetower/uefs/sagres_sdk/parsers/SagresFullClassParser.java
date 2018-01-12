package com.forcetower.uefs.sagres_sdk.parsers;

import android.util.Log;
import android.util.Pair;

import com.forcetower.uefs.helpers.Utils;
import com.forcetower.uefs.sagres_sdk.SagresConstants;
import com.forcetower.uefs.sagres_sdk.SagresPortalSDK;
import com.forcetower.uefs.sagres_sdk.domain.SagresAccess;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassDetails;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassGroup;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassItem;
import com.forcetower.uefs.sagres_sdk.domain.SagresClassTime;
import com.forcetower.uefs.sagres_sdk.utility.SagresConnector;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

import static com.forcetower.uefs.Constants.APP_TAG;

/**
 * Created by João Paulo on 14/12/2017.
 */

public class SagresFullClassParser {
    private static List<SagresClassDetails> classDetailsList;

    public static List<SagresClassDetails> loginConnectAndGetClassesDetails(String specificSemester, String specificCode, String specificGroup, boolean draftOnly) {
        if (SagresAccess.getCurrentAccess() == null) {
            Log.e(SagresPortalSDK.SAGRES_SDK_TAG, "Invalid Access");
            return new ArrayList<>();
        }
        SagresAccess access = SagresAccess.getCurrentAccess();
        try {
            JSONObject object = SagresConnector.login(access.getUsername(), access.getPassword());
            if (object.has("error")) {
                Log.e(SagresPortalSDK.SAGRES_SDK_TAG, "Login error when trying to get info");
                return new ArrayList<>();
            }
            return connectAndGetClassesDetails(specificSemester, specificCode, specificGroup, draftOnly);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static List<SagresClassDetails> connectAndGetClassesDetails(String specificSemester, String specificCode, String specificGroup, boolean draftOnly) {
        Request request = new Request.Builder()
                .url(SagresConstants.SAGRES_DIARY_PAGE)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();

        Call call = SagresPortalSDK.getHttpClient().newCall(request);
        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                String html = response.body().string();
                Document document = Jsoup.parse(html);
                return getClassesDetails(document, specificSemester, specificCode, specificGroup, draftOnly);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static List<SagresClassDetails> getClassesDetails(Document document, String specificSemester, String specificCode, String specificGroup, boolean draftOnly) {
        document.charset(Charset.forName("ISO-8859-1"));
        classDetailsList = new ArrayList<>();
        List<Pair<FormBody.Builder,String>> builderList = new ArrayList<>();

        Elements classes = document.select("section[class=\"webpart-aluno-item\"]");
        for (Element classDet : classes) {
            String title = classDet.selectFirst("a[class=\"webpart-aluno-nome cor-destaque\"]").text();
            String period = classDet.selectFirst("span[class=\"webpart-aluno-periodo\"]").text();
            String credits = classDet.select("span[class=\"webpart-aluno-codigo\"]").text();
            credits = credits.replaceAll("[^\\d]", "");

            Element studentLinks = classDet.selectFirst("div[class=\"webpart-aluno-links webpart-aluno-links-up\"]");
            if (studentLinks == null) studentLinks = classDet.selectFirst("div[class=\"webpart-aluno-links webpart-aluno-links-down\"]");
            Element misses = studentLinks.child(1);
            Element missesSpan = misses.selectFirst("span");
            String missedClasses = missesSpan.text();

            String situation = null;
            Element situationPart = classDet.selectFirst("div[class=\"webpart-aluno-resultado\"]");
            if (situationPart == null) situationPart = classDet.selectFirst("div[class=\"webpart-aluno-resultado estado-sim\"]");
            if (situationPart == null) situationPart = classDet.selectFirst("div[class=\"webpart-aluno-resultado estado-nao\"]");
            if (situationPart != null && situationPart.children().size() == 2) {
                situation = situationPart.children().get(1).text();
                situation = situation.toLowerCase();
                situation = Utils.toTitleCase(situation);
                if (situation.equalsIgnoreCase("Não existe resultado final divulgado pelo professor.")) {
                    situation = "Em aberto";
                }
            }

            String last = "";
            String next = "";
            Elements lastAndNextClasses = classDet.select("div[class=\"webpart-aluno-detalhe\"]");
            if (lastAndNextClasses.size() > 0) {
                Element lastSpan = lastAndNextClasses.get(0).selectFirst("span");
                last = lastSpan.text();
            }

            if (lastAndNextClasses.size() > 1) {
                Element nextSpan = lastAndNextClasses.get(1).selectFirst("span");
                next = nextSpan.text();
            }

            int codePos = title.indexOf("-");
            String code = title.substring(0, codePos).trim();
            String name = title.substring(codePos + 1);

            SagresClassDetails details = new SagresClassDetails(name, code);
            details.setSemester(period);
            details.setCredits(credits);
            details.setMissedClasses(missedClasses);
            details.setLastClass(last);
            details.setNextClass(next);
            details.setSituation(situation);

            Elements elements = document.select("input[value][type=\"hidden\"]");
            Element ul = classDet.selectFirst("ul");

            if (ul != null) {
                Elements lis = ul.select("li");
                for (Element li : lis) {
                    Element element = li.selectFirst("a[href]");
                    String values = element.attr("href");
                    int start = values.indexOf("'");
                    values = values.substring(start + 1);
                    int end = values.indexOf("'");

                    values = values.substring(0, end);
                    String type = element.text();
                    int refGroupPos = type.lastIndexOf("(");
                    type = type.substring(0, refGroupPos).trim();

                    FormBody.Builder builderIn = new FormBody.Builder();
                    for (Element elementIn : elements) {
                        String key = elementIn.attr("id");
                        String value = elementIn.attr("value");
                        builderIn.add(key, value);
                    }
                    builderIn.add("__EVENTTARGET", values);
                    if (specificSemester == null || period.equalsIgnoreCase(specificSemester)) {
                        if (specificCode == null || code.equalsIgnoreCase(specificCode)) {
                            if (specificGroup == null || type.equalsIgnoreCase(specificGroup)) {
                                builderList.add(new Pair<>(builderIn, period));
                            }
                        }
                    }

                    SagresClassGroup classGroup = new SagresClassGroup(null, type, null, null, null, null);
                    classGroup.setSagresConnectCode(values);
                    details.addGroup(classGroup);
                }
            } else {
                Element webPart = classDet.selectFirst("div[class=\"webpart-dropdown webpart-dropdown-up\"]");
                Element anchor = webPart.selectFirst("a[href]");
                String values = anchor.attr("href");
                int start = values.indexOf("'");
                values = values.substring(start + 1);
                int end = values.indexOf("'");

                values = values.substring(0, end);

                FormBody.Builder builder = new FormBody.Builder();
                builder.add("__EVENTTARGET", values);

                for (Element element : elements) {
                    String key = element.attr("id");
                    String value = element.attr("value");
                    builder.add(key, value);
                }
                if (specificSemester == null || period.equalsIgnoreCase(specificSemester)) {
                    if (specificCode == null ||code.equalsIgnoreCase(specificCode)) {
                        builderList.add(new Pair<>(builder, period));
                    }
                }

                SagresClassGroup classGroup = new SagresClassGroup(null, null, credits, null, null, null);
                classGroup.setSagresConnectCode(values);
                details.addGroup(classGroup);
            }

            classDetailsList.add(details);
        }

        if (!draftOnly) {
            for (Pair<FormBody.Builder, String> pair : builderList) {
                try {
                    preConnect(pair.first, pair.second);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return classDetailsList;
    }

    private static void preConnect(FormBody.Builder builder, String semester) {
        Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Pre connect LIVE");
        Request request = new Request.Builder()
                .url(SagresConstants.SAGRES_DIARY_PAGE)
                .post(builder.build())
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();

        Call call = SagresPortalSDK.getHttpClient().newCall(request);

        try {
            Response response = call.execute();
            if (response.isSuccessful()) {
                String html = response.body().string();
                Document document = Jsoup.parse(html);
                document.charset(Charset.forName("ISO-8859-1"));

                FormBody.Builder builderIn = new FormBody.Builder();
                Elements elements = document.select("input[value][type=\"hidden\"]");
                builderIn.add("ctl00$MasterPlaceHolder$RowsPerPage1$ddMostrar", "0");
                for (Element elementIn : elements) {
                    String id = elementIn.attr("id");
                    String val = elementIn.attr("value");
                    builderIn.add(id, val);
                }

                findDetails(builderIn, semester);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void findDetails(FormBody.Builder builder, String semester) {
        Request request = new Request.Builder()
                .url(SagresConstants.SAGRES_CLASS_PAGE)
                .post(builder.build())
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("cache-control", "no-cache")
                .build();

        Call call = SagresPortalSDK.getHttpClient().newCall(request);

        try {
            Response response = call.execute();
            if (!response.isSuccessful()) {
                Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Error connecting code: " + response.code());
                return;
            }

            String html = response.body().string();
            Document document = Jsoup.parse(html);
            document.charset(Charset.forName("ISO-8859-1"));

            Element elementName = document.selectFirst("h2[class=\"cabecalho-titulo\"]");
            String classNameFull = elementName.text();

            int codePos = classNameFull.indexOf("-");
            String code = classNameFull.substring(0, codePos).trim();
            int groupPos = classNameFull.lastIndexOf("(");
            String group = classNameFull.substring(groupPos);
            int refGroupPos = group.lastIndexOf("-");
            String refGroup = group.substring(refGroupPos + 1, group.length() - 1).trim();
            String name = classNameFull.substring(codePos + 1, groupPos).trim();

            SagresClassGroup classGroup = getGroupByCode(code, refGroup, semester);
            if (classGroup == null) classGroup = desperateMeasures(code, name, refGroup, semester);

            classGroup.setType(refGroup);

            String semesterByName;
            String classCredits = "";
            String missLimits;
            String classPeriod;
            String department;

            for (Element element : document.select("div[class=\"cabecalho-dado\"]")) {
                Element b = element.child(0);
                String bText = b.text();
                if (bText.equalsIgnoreCase("Período:")) {
                    semesterByName = element.child(1).text();
                    classGroup.setSemester(semesterByName);
                }

                else if (bText.equalsIgnoreCase("Carga horária:") && classCredits.isEmpty()) {
                    classCredits = element.child(1).text();
                    classCredits = classCredits.replaceAll("[^\\d]", "").trim();
                    classGroup.setCredits(classCredits);
                }

                else if (bText.equalsIgnoreCase("Limite de Faltas:")) {
                    missLimits = element.child(1).text();
                    missLimits = missLimits.replaceAll("[^\\d]", "").trim();
                    classGroup.setMissLimit(missLimits);
                }

                else if (bText.equalsIgnoreCase("Período de aulas:")) {
                    classPeriod = element.selectFirst("span").text();
                    classGroup.setPeriod(classPeriod);
                }

                else if (bText.equalsIgnoreCase("Departamento:")) {
                    department = Utils.toTitleCase(element.child(1).text());
                    classGroup.setDepartment(department);
                }

                else if (bText.equalsIgnoreCase("Horário:")) {
                    for (Element classTime : element.select("div[class=\"cabecalho-horario\"]")) {
                        String day = classTime.child(0).text();
                        String start = classTime.child(1).text();
                        String end = classTime.child(3).text();
                        classGroup.addClassTime(new SagresClassTime(day, start, end));
                    }
                }
            }

            String teacher = "";
            Element element = document.selectFirst("div[class=\"cabecalho-dado nome-capitalizars\"]");
            if (element != null) {
                element = element.selectFirst("span");
                if (element != null) teacher = Utils.toTitleCase(element.text());
            }
            classGroup.setTeacher(teacher);

            Elements trs = document.select("tr[class]");

            List<SagresClassItem> classItems = new ArrayList<>();
            for (int i = 1; i < trs.size(); i++) {
                Element tr = trs.get(i);
                Elements tds = tr.select("td");
                if (!tds.isEmpty()) {
                    SagresClassItem classItem = getFromTDs(tds);
                    if (classItem != null) classItems.add(classItem);
                }
            }
            classGroup.setClasses(classItems);
            classGroup.setDraft(false);
            Log.i(SagresPortalSDK.SAGRES_SDK_TAG, "Finished details of " + classNameFull);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static SagresClassGroup desperateMeasures(String code, String name, String refGroup, String semester) {
        Log.e(SagresPortalSDK.SAGRES_SDK_TAG, "Desperate measures for class: " + name);
        SagresClassDetails details = new SagresClassDetails(code, name);
        details.setSemester(semester);

        SagresClassGroup classGroup = new SagresClassGroup(null, refGroup, null, null, null, null);
        details.addGroup(classGroup);
        classDetailsList.add(details);
        return classGroup;
    }

    private static SagresClassGroup getGroupByCode(String code, String grouping, String semester) {
        if (classDetailsList != null)
            for (SagresClassDetails classGroup : classDetailsList)
                if (classGroup.getCode().equalsIgnoreCase(code) && classGroup.getSemester().equalsIgnoreCase(semester)) {
                    List<SagresClassGroup> groups = classGroup.getGroups();
                    for (SagresClassGroup group : groups) {
                        if (group.getType() == null && groups.size() == 1)
                            return group;

                        if (group.getType().contains(grouping))
                            return group;
                    }
                }

        return null;
    }

    private static SagresClassItem getFromTDs(Elements tds) {
        try {
            String number = tds.get(0).text();
            String situation = tds.get(1).text();
            String date = tds.get(2).text();
            String description = tds.get(3).text();
            String materials = tds.get(5).text();
            return new SagresClassItem(number, situation, description, date, materials);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return null;
    }
}
