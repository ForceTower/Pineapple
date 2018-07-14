package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db.entity.MessageUNES;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MessageUNESDao {
    @Insert(onConflict = REPLACE)
    void insert(MessageUNES message);

    @Query("SELECT * FROM MessageUNES ORDER BY create_date DESC")
    LiveData<List<MessageUNES>> getAllMessages();

    @Delete
    void delete(MessageUNES message);

    @Query("DELETE FROM MessageUNES")
    void deleteAll();
}
