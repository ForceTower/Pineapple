package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.AScrapDao;
import com.forcetower.uefs.database.entities.AScrap;

import java.util.List;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class ScrapRepository {
    private final AScrapDao dao;

    public ScrapRepository(AScrapDao dao) {
        this.dao = dao;
    }

    public List<AScrap> getAllScraps() {
        return dao.getAllScraps();
    }

    public List<AScrap> getScraps(String message, String sender) {
        return dao.getScraps(message, sender);
    }

    public void insertScraps(AScrap... scraps) {
        dao.insertScraps(scraps);
    }

    public void deleteScrap(AScrap scrap) {
        dao.deleteScrap(scrap);
    }

    public void deleteAllScraps() {
        dao.deleteAllScraps();
    }
}
