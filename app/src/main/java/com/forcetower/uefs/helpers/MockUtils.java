package com.forcetower.uefs.helpers;

import com.forcetower.uefs.database.entities.ATodoItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by João Paulo on 30/12/2017.
 */

public class MockUtils {
    public static List<ATodoItem> getTodoList() {
        List<ATodoItem> mockList = new ArrayList<>();
        ATodoItem completed = new ATodoItem("TEC999", "Nada de interessante", "12/12/1212", true);
        completed.setCompleted(true);
        mockList.add(new ATodoItem("TEC500", "Estudar bastante para a prova", "24/03/2017", true));
        mockList.add(completed);
        mockList.add(new ATodoItem("TEC500", "Fazer a lista de exercicios", "12/08/2017", true));
        mockList.add(new ATodoItem("EXA869", "Atividade sobre Concorrência", null, false));
        mockList.add(new ATodoItem("TEC500", "Estudar bastante para a prova", "24/03/2017", true));
        mockList.add(new ATodoItem("TEC500", "Fazer a lista de exercicios", "12/08/2017", true));
        mockList.add(new ATodoItem("EXA869", "Atividade sobre Concorrência", null, false));
        mockList.add(new ATodoItem("TEC500", "Estudar bastante para a prova", "24/03/2017", true));
        mockList.add(completed);
        mockList.add(new ATodoItem("TEC500", "Fazer a lista de exercicios", "12/08/2017", true));
        mockList.add(new ATodoItem("EXA869", "Atividade sobre Concorrência", null, false));
        mockList.add(new ATodoItem("TEC500", "Estudar bastante para a prova", "24/03/2017", true));
        mockList.add(new ATodoItem("TEC500", "Fazer a lista de exercicios", "12/08/2017", true));
        mockList.add(new ATodoItem("EXA869", "Atividade sobre Concorrência", null, false));
        mockList.add(new ATodoItem("TEC500", "Estudar bastante para a prova", "24/03/2017", true));
        mockList.add(new ATodoItem("TEC500", "Fazer a lista de exercicios", "12/08/2017", true));
        mockList.add(new ATodoItem("EXA869", "Atividade sobre Concorrência", null, false));
        mockList.add(completed);
        mockList.add(new ATodoItem("TEC500", "Estudar bastante para a prova", "24/03/2017", true));
        mockList.add(new ATodoItem("TEC500", "Fazer a lista de exercicios", "12/08/2017", true));
        mockList.add(new ATodoItem("EXA869", "Atividade sobre Concorrência", null, false));
        mockList.add(new ATodoItem("TEC500", "Estudar bastante para a prova", "24/03/2017", true));
        mockList.add(new ATodoItem("TEC500", "Fazer a lista de exercicios", "12/08/2017", true));
        mockList.add(new ATodoItem("EXA869", "Atividade sobre Concorrência", null, false));
        mockList.add(new ATodoItem("TEC500", "Estudar bastante para a prova", "24/03/2017", true));
        mockList.add(new ATodoItem("TEC500", "Fazer a lista de exercicios", "12/08/2017", true));
        mockList.add(new ATodoItem("EXA869", "Atividade sobre Concorrência", null, false));
        return mockList;
    }
}
