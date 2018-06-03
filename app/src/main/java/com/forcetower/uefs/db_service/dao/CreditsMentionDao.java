package com.forcetower.uefs.db_service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.forcetower.uefs.db_service.entity.CreditsMention;
import com.forcetower.uefs.db_service.entity.Mention;
import com.forcetower.uefs.db_service.helper.CreditAndMentions;

import java.util.List;

/**
 * Created by João Paulo on 03/06/2018.
 */
@Dao
public interface CreditsMentionDao {
    @Transaction
    @Query("SELECT * FROM CreditsMention")
    LiveData<List<CreditAndMentions>> getCreditsWithMentions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(CreditsMention creditsMention);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long[] insertAll(CreditsMention... creditsMention);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFull(CreditsMention mention, List<Mention> mentions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insert(List<CreditsMention> creditsMention);

    @Query("SELECT * FROM CreditsMention")
    LiveData<List<CreditsMention>> getAllCreditsMention();

    @Delete
    void delete(CreditsMention creditsMention);

    @Query("DELETE FROM CreditsMention")
    void deleteAll();
}
