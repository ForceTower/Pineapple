package com.forcetower.uefs.sgrs.parsers;

import com.forcetower.uefs.db.entity.DisciplineClassMaterialLink;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by João Paulo on 14/05/2018.
 */
public class SagresMaterialsParser {

    public static List<DisciplineClassMaterialLink> getMaterials(Document document, int classId) {
        List<DisciplineClassMaterialLink> materials = new ArrayList<>();
        Elements elements = document.select("label[class=\"material_apoio_arquivo\"]");
        for (Element element : elements) {
            elementProcessing(element, materials, classId);
        }

        elements = document.select("label[class=\"material_apoio_url\"]");
        for (Element element : elements) {
            elementProcessing(element, materials, classId);
        }
        return materials;
    }

    private static void elementProcessing(Element element, List<DisciplineClassMaterialLink> materials, int classId) {
        Element a = element.selectFirst("a");
        String link = a.attr("href").isEmpty() ? a.attr("href") : a.attr("HREF");
        Timber.d("Link: " + link);
        String name = "Arquivo";

        Element parent = element.parent();
        if (parent != null) {
            parent = parent.parent();
            if (parent != null) {
                Element elName = parent.selectFirst("td");
                name = elName.text();
                Timber.d("Name: " + elName.text());
            }
        }

        materials.add(new DisciplineClassMaterialLink(classId, name, link));
    }
}
