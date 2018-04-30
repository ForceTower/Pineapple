package com.forcetower.uefs.db_service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.forcetower.uefs.db_service.entity.Account;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by João Paulo on 30/04/2018.
 */
@Dao
public interface AccountDao {
    @Query("SELECT * FROM Account")
    LiveData<List<Account>> getAccounts();

    @Query("SELECT * FROM Account WHERE username = :username")
    LiveData<Account> getAccount(String username);

    @Update
    void update(Account account);

    @Insert(onConflict = REPLACE)
    void insert(Account... accounts);

    @Delete
    void delete(Account account);
}
