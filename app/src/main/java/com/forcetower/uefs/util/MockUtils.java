package com.forcetower.uefs.util;

import android.support.annotation.NonNull;

import com.forcetower.uefs.db.entity.CreditsMention;
import com.forcetower.uefs.db.entity.DisciplineClassLocation;
import com.forcetower.uefs.db.entity.Mention;

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

        Mention bandejao = new Mention("Fonte dos Dados", "http://bit.ly/bandejaouefs");

        mentions.add(new CreditsMention("Ícone do Aplicativo", Collections.singletonList(lokisley)));
        mentions.add(new CreditsMention("Nome do Aplicativo", Arrays.asList(teixeira, lokisley)));
        mentions.add(new CreditsMention("Bandejão UEFS", Collections.singletonList(bandejao)));

        return mentions;
    }
}
