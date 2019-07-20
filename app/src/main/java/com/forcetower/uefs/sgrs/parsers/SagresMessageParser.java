package com.forcetower.uefs.sgrs.parsers;

import com.forcetower.uefs.db.entity.Message;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 06/03/2018.
 */

public class SagresMessageParser {
    public static List<Message> getMessages(Document document) {
        List<Message> messages = new ArrayList<>();
        Elements articles = document.select("article");
        for (Element article : articles) {
            String clazz = article.selectFirst("span[class=\"recado-escopo\"]").text().trim();
            String dated = article.selectFirst("span[class=\"recado-data\"]").text().trim();
            String message = article.selectFirst("p[class=\"recado-texto\"]").wholeText().trim();
            String prefixDescription = "Descrição do Recado:";
            if (message.startsWith(prefixDescription)) {
                message = message.substring(prefixDescription.length()).trim();
            }

            String from = article.selectFirst("i[class=\"recado-remetente\"]").text().trim();
            String prefixFrom = "De";
            if (from.startsWith(prefixFrom)) {
                from = from.substring(prefixFrom.length()).trim();
            }
            messages.add(new Message(from, message, dated, clazz));
        }

        return messages;
    }

}
