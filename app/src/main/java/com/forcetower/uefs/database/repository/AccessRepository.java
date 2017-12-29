package com.forcetower.uefs.database.repository;

import com.forcetower.uefs.database.dao.AAccessDao;
import com.forcetower.uefs.database.entities.AAccess;

import javax.inject.Inject;

/**
 * Created by João Paulo on 29/12/2017.
 */

public class AccessRepository {
    private AAccessDao dao;

    @Inject
    public AccessRepository(AAccessDao dao) {
        this.dao = dao;
    }

    public AAccess getUser() {
        return dao.getUser();
    }

    public void insertAccess(AAccess... access) {
        dao.insertAccess(access);
    }

    void deleteAccess(AAccess access) {
        dao.deleteAccess(access);
    }
}
