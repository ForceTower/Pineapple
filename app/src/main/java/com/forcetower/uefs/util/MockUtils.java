package com.forcetower.uefs.util;

import android.support.annotation.NonNull;

import com.forcetower.uefs.db.entity.CreditsMention;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.Mention;
import com.forcetower.uefs.db.entity.QuestionAnswer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class MockUtils {

    @NonNull
    public static List<DisciplineClassLocation> getClassLocations() {
        List<DisciplineClassLocation> locations = new ArrayList<>();

        locations.add(new DisciplineClassLocation("13:30", "15:30", "SEG", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas", "TEC402", "T01"));
        locations.add(new DisciplineClassLocation("13:30", "15:30", "QUI", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas", "TEC402", "T01"));
        locations.add(new DisciplineClassLocation("15:30", "17:30", "SEG", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas", "TEC402", "T01"));
        locations.add(new DisciplineClassLocation("15:30", "17:30", "SEX", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas", "TEC402", "T01"));
        locations.add(new DisciplineClassLocation("15:30", "17:30", "QUI", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas", "TEC402", "T01"));
        locations.add(new DisciplineClassLocation("15:30", "17:30", "QUA", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas", "TEC402", "T01"));
        locations.add(new DisciplineClassLocation("13:30", "15:30", "SEX", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas", "TEC402", "T01"));
        locations.add(new DisciplineClassLocation("09:30", "11:30", "SEX", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas", "TEC402", "T01"));
        locations.add(new DisciplineClassLocation("09:30", "11:30", "QUA", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas", "TEC402", "T01"));
        locations.add(new DisciplineClassLocation("13:30", "15:30", "QUA", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas", "TEC402", "T01"));

        return locations;
    }

    public static List<CreditsMention> getCredits() {
        List<CreditsMention> mentions = new ArrayList<>();

        Mention lokisley = new Mention("Lokisley Oliveira", "https://www.facebook.com/Lokisley");
        Mention teixeira = new Mention("Matheus Teixeira", "https://www.facebook.com/teixeirista");
        Mention kuchuki  = new Mention("Marcus \"Kuchuki\" Soares", "https://www.facebook.com/marcus.aldrey");

        Mention bandejao = new Mention("Fonte dos Dados", "http://bit.ly/bandejaouefs");

        mentions.add(new CreditsMention("Ícone do Aplicativo", Collections.singletonList(lokisley)));
        mentions.add(new CreditsMention("Nome do Aplicativo", Arrays.asList(teixeira, lokisley, kuchuki)));
        mentions.add(new CreditsMention("Bandejão UEFS", Collections.singletonList(bandejao)));

        return mentions;
    }

    public static List<QuestionAnswer> getFAQ() {
        List<QuestionAnswer> faq = new ArrayList<>();

        faq.add(new QuestionAnswer(0, "Onde está o aplicativo para iOS?",
                "Quando o assunto é a Apple Store, eu tenho 2 problemas.\n" +
                "1) Para compilar e gerar o aplicativo eu preciso ter acesso a um Mac (eu não tenho um Mac)\n" +
                "2) A licença de desenvovedor Apple custa 100 dólares por ano :/\n" +
                "\n" +
                "O problema número 1 eu consigo contornar pedindo para gerar o aplicativo no Mac de amigos, mas o problema número 2 é mais difícil.\n" +
                "\n" +
                "Por isso até hoje não temos uma versão do aplicativo para usuários de iOS, apesar de eu querer bastante publicar para as 2 plataformas\n" +
                "\n" +
                "Ainda estou pensando se existe alguma maneira de eu conseguir publicar por lá, mas a priori, não há estimativas." +
                "\n\n" +
                "Caso tenha alguma ideia, pode mandar que eu leio :v"));

        faq.add(new QuestionAnswer(1, "Quando vai ter poder baixar os arquivos enviados pelos professores?",
                "Essa parte eu tô tentando ainda, eu ainda não entendi como é que o Sagres funciona nessa parte direito porque ele não segue um padrão nessa parte.\n\n" +
                "Mas sim, é um recurso que espero conseguir trazer em breve (até pq é só para isso que eu abro o Sagres recentemente, e também na época da matrícula...) "));

        faq.add(new QuestionAnswer(2, "Como o aplicativo funciona?",
                "Bem, de tempos em tempos o aplicativo se conecta à sua conta no Sagres baixa as informações e verifica se ocorreu alguma mudança nos dados.\n" +
                "\n" +
                "Em caso positivo, ele tenta determinar o que mudou e então irá gerar uma notificação de mensagem ou de notas.\n\n" +
                "Se você faz parte de uma disciplina que tem prática e teórica, note que o que o nome das avaliações práticas de todas as turmas são as mesmas, esta é a parte mais sensível do aplicativo e em casos como este ele tentará casar as avaliações que ficam na mesma data (Caso a prática troque a data teremos uma situação bem interessante)."));

        faq.add(new QuestionAnswer(3, "As minhas conquistas de nota não desbloqueiam, u q tá contesseno?",
                "Em casos como este é bem possível que as notas de semestres anteriores não foram carregadas." +
                        "\n\n" +
                        "Relogue e tente novamente, se não funcionar ainda assim, me manda uma mensagem"));

        return faq;
    }
}
