package com.forcetower.uefs.util;

import android.support.annotation.NonNull;

import com.forcetower.uefs.db.entity.DisciplineClassLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 07/03/2018.
 */

public class MockUtils {

    @NonNull
    public static List<DisciplineClassLocation> getClassLocations() {
        List<DisciplineClassLocation> locations = new ArrayList<>();

        locations.add(new DisciplineClassLocation("13:30", "15:30", "SEG", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas"));
        locations.add(new DisciplineClassLocation("13:30", "15:30", "QUI", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas"));
        locations.add(new DisciplineClassLocation("15:30", "17:30", "SEG", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas"));
        locations.add(new DisciplineClassLocation("15:30", "17:30", "SEX", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas"));
        locations.add(new DisciplineClassLocation("15:30", "17:30", "QUI", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas"));
        locations.add(new DisciplineClassLocation("15:30", "17:30", "QUA", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas"));
        locations.add(new DisciplineClassLocation("13:30", "15:30", "SEX", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas"));
        locations.add(new DisciplineClassLocation("09:30", "11:30", "SEX", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas"));
        locations.add(new DisciplineClassLocation("09:30", "11:30", "QUA", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas"));
        locations.add(new DisciplineClassLocation("13:30", "15:30", "QUA", "PAT34", "UEFS", "Módulo 3", "Sinais e Sistemas"));

        return locations;
    }
}
