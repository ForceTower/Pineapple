package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db.entity.Message;

import java.util.List;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Dao
public interface MessageDao {
    @Query("SELECT * FROM Message")
    LiveData<List<Message>> getAllMessages();

    @Query("SELECT * FROM Message WHERE notified = 0")
    List<Message> getAllUnnotifiedMessages();

    @Query("SELECT * FROM Message WHERE message LIKE :message AND sender LIKE :sender")
    LiveData<List<Message>> getMessagesLike(String message, String sender);

    @Query("SELECT * FROM Message WHERE LOWER(message) = LOWER(:message) AND LOWER(sender) = LOWER(:sender)")
    List<Message> getMessagesDirectLike(String message, String sender);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMessages(Message... messages);

    @Delete
    void deleteMessage(Message message);

    @Query("DELETE FROM Message")
    void deleteAllMessages();

    @Query("UPDATE Message SET notified = 1")
    void clearAllNotifications();
}
