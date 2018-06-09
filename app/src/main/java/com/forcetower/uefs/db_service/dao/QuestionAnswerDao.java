package com.forcetower.uefs.db_service.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.forcetower.uefs.db_service.entity.QuestionAnswer;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by João Paulo on 09/06/2018.
 */
@Dao
public interface QuestionAnswerDao {
    @Query("SELECT * FROM QuestionAnswer")
    LiveData<List<QuestionAnswer>> getFAQ();

    @Insert(onConflict = REPLACE)
    void insert(List<QuestionAnswer> questions);

    @Delete
    void delete(QuestionAnswer question);

    @Query("DELETE FROM QuestionAnswer")
    void deleteAllQuestions();
}
