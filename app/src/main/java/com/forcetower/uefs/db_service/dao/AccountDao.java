package com.forcetower.uefs.db_service.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.forcetower.uefs.db_service.entity.Account;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

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