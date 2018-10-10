package com.forcetower.uefs.db.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db.entity.Message;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by João Paulo on 05/03/2018.
 */
@Dao
public abstract class  MessageDao {
    @Query("SELECT * FROM Message")
    public abstract LiveData<List<Message>> getAllMessages();

    @Query("SELECT * FROM Message WHERE notified = 0")
    public abstract List<Message> getAllUnnotifiedMessages();

    @Query("SELECT * FROM Message WHERE message LIKE :message AND sender LIKE :sender")
    public abstract LiveData<List<Message>> getMessagesLike(String message, String sender);

    @Query("SELECT * FROM Message WHERE LOWER(message) = LOWER(:message) AND LOWER(sender) = LOWER(:sender)")
    public abstract List<Message> getMessagesDirectLike(String message, String sender);

    public void insertMessages(Message... messages) {
        for (Message message : messages) {
            if (message.getReceiveTime() == 0) message.setReceiveTime(System.currentTimeMillis());
        }
        insertAll(messages);
    }

    @Insert(onConflict = REPLACE)
    public abstract void insertAll(Message... messages);

    @Delete
    public abstract void deleteMessage(Message message);

    @Query("DELETE FROM Message")
    public abstract void deleteAllMessages();

    @Query("UPDATE Message SET notified = 1")
    public abstract void clearAllNotifications();
}
