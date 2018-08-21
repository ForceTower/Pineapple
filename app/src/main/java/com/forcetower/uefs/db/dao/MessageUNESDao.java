package com.forcetower.uefs.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.forcetower.uefs.db.entity.MessageUNES;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

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
