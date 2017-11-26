package com.forcetower.uefs.sagres_sdk.parsers;

import com.forcetower.uefs.sagres_sdk.domain.GradeInfo;
import com.forcetower.uefs.sagres_sdk.domain.GradeSection;
import com.forcetower.uefs.sagres_sdk.domain.SagresGrade;

import java.util.HashMap;

/**
 * Created by João Paulo on 25/11/2017.
 */

public class SagresGradesParser {
    private static final String CARD_REPORT_PATTERN = "<div id=\"divBoletins\">";
    private static final String GRADES_PATTERN = "class=\"boletim-notas\"";
    private static final String TD_CLASS_DARK = "<td class=\"negrito\">";

    private static final String TABLE_PATTERN = "<table";
    private static final String TABLE_END_PATTERN = "</table>";
    private static final String T_BODY_PATTERN = "<tbody>";
    private static final String T_BODY_END_PATTERN = "</tbody>";
    private static final String TR_PATTERN = "<tr>";
    private static final String TR_END_PATTERN = "</tr>";

    private static HashMap<String, SagresGrade> sagresGradesHash;

    public static HashMap<String, SagresGrade> getGrades(String html) {
        sagresGradesHash = new HashMap<>();

        int index = html.indexOf(CARD_REPORT_PATTERN);
        if (index == -1) {
            return null;
        }

        String gradesDiv = html.substring(index + CARD_REPORT_PATTERN.length()).trim();
        extractEachClass(gradesDiv);

        return sagresGradesHash;
    }

    private static void extractEachClass(String gradesDiv) {
        int index = 0;

        while (index < gradesDiv.length()) {
            int position = gradesDiv.indexOf(GRADES_PATTERN, index);
            if (position == -1) {
                return;
            }

            int nameStart = gradesDiv.indexOf("ItemBoletim_lblDescricao", index);
            int nameEnd = gradesDiv.indexOf("</span>", nameStart);

            String name = gradesDiv.substring(nameStart, nameEnd);

            nameStart = name.indexOf(">") + 1;
            name = name.substring(nameStart);
            SagresGrade objectGrade = new SagresGrade(name);

            position += GRADES_PATTERN.length() + 1;
            String div = gradesDiv.substring(position);

            String gradeTable = extractGradeTable(div);

            if (gradeTable != null) {
                processTBodyForGrades(gradeTable, objectGrade);
                int tFootStart = gradeTable.indexOf("<tfoot>");
                int tFootEnd = gradeTable.indexOf("</tfoot>");

                String tFoot = gradeTable.substring(tFootStart + 8, tFootEnd);
                boolean hasFinalScore = tFoot.contains("ItemBoletim_lblMediaFinal");
                if (hasFinalScore) {
                    int fScoreStart = tFoot.indexOf("ItemBoletim_lblMediaFinal");
                    int fScoreEnd = tFoot.indexOf("</span>", fScoreStart);
                    String finalScore = tFoot.substring(fScoreStart, fScoreEnd);
                    fScoreStart = finalScore.indexOf(">") + 1;
                    finalScore = finalScore.substring(fScoreStart);
                    objectGrade.setFinalScore(finalScore);
                } else {
                    objectGrade.setFinalScore("Não foi divulgado");
                }
            }

            index = position;

            sagresGradesHash.put(objectGrade.getClassCode(), objectGrade);
        }
    }

    private static String extractGradeTable(String div) {
        int tableStart = div.indexOf(TABLE_PATTERN);
        int tableEnd = div.indexOf(TABLE_END_PATTERN);

        if (tableStart == -1) {
            return null;
        }

        return div.substring(tableStart + TABLE_PATTERN.length() + 1, tableEnd).trim();
    }

    private static void processTBodyForGrades(String tBody, SagresGrade objectGrade) {
        int index = 0;
        String currentHeader = "";
        GradeSection currentGSection = new GradeSection("");

        while (index < tBody.length()) {
            //TODO this skips the line: <tr class="boletim-linha-destaque"> FIX it
            int position = tBody.indexOf(TR_PATTERN, index);
            if (position == -1) {
                if (currentGSection.getGrades().size() != 0) {
                    objectGrade.addSection(currentGSection);
                }
                return;
            }
            int finish = tBody.indexOf(TR_END_PATTERN, position);
            String tr = tBody.substring(position + TR_PATTERN.length(), finish).trim();

            if (tr.contains(TD_CLASS_DARK)) {
                String nHeader = extractGradeHeader(tr);

                if (nHeader != null && !nHeader.equals(currentHeader)) {
                    currentHeader = nHeader;
                    if (currentGSection.getGrades().size() != 0) {
                        objectGrade.addSection(currentGSection);
                    }

                    currentGSection = new GradeSection(currentHeader);
                }
            } else if (tr.contains("<span")) {
                GradeInfo gradeInfo = extractGradeAndInfo(tr);
                currentGSection.addGradeInfo(gradeInfo);
            }

            position += TR_PATTERN.length();
            index = position;
        }

        if (currentGSection.getGrades().size() != 0) {
            objectGrade.addSection(currentGSection);
        }
    }

    private static GradeInfo extractGradeAndInfo(String tr) {
        //Date
        int tdDateStart = tr.indexOf("ResumoGrupo_lblDataAvaliacao\"");
        int tdDateEnd = tr.indexOf("</span>", tdDateStart);

        String date = tr.substring(tdDateStart, tdDateEnd).trim();
        tdDateStart = date.indexOf(">") + 1;
        date = date.substring(tdDateStart).trim();

        //Identification
        int tdIdStart = tr.indexOf("ResumoGrupo_lblDescricaoAvaliacao\"");
        int tdIdEnd = tr.indexOf("</span>", tdIdStart);
        String identification = tr.substring(tdIdStart, tdIdEnd).trim();
        tdIdStart = identification.indexOf(">") + 1;
        identification = identification.substring(tdIdStart).trim();

        //Grade
        int tdGradeStart = tr.indexOf("<span", tdIdEnd);
        int tdGradeEnd = tr.indexOf("</span>", tdGradeStart);
        String grade = tr.substring(tdGradeStart, tdGradeEnd).trim();
        tdGradeStart = grade.indexOf(">") + 1;
        grade = grade.substring(tdGradeStart).trim();

        return new GradeInfo(identification, grade, date);
    }

    private static String extractGradeHeader(String tr) {
        int start = tr.indexOf("<span");
        int end = tr.indexOf("</span>");

        if (start == -1) {
            return null;
        } else {
            String substring = tr.substring(start + 5, end);
            start = substring.indexOf(">") + 1;
            substring = substring.substring(start).trim();
            return substring;
        }
    }
}
