package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.ADisciplineClassItemDao;
import com.forcetower.uefs.database.entities.ADisciplineClassItem;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class DisciplineClassItemRepository {
    private final ADisciplineClassItemDao dao;

    public DisciplineClassItemRepository(ADisciplineClassItemDao dao) {
        this.dao = dao;
    }

    public List<ADisciplineClassItem> getAllDisciplineClassItems() {
        return dao.getAllDisciplineClassItems();
    }

    public List<ADisciplineClassItem> getDisciplineClassItemsFromGroup(int groupId) {
        return dao.getDisciplineClassItemsFromGroup(groupId);
    }

    public void insertClassItem(ADisciplineClassItem... classItems) {
        dao.insertClassItem(classItems);
    }

    public void deleteClassItem(ADisciplineClassItem classItem) {
        dao.deleteClassItem(classItem);
    }

    public void deleteAllDisciplineClassItems() {
        dao.deleteAllDisciplineClassItems();
    }
}
