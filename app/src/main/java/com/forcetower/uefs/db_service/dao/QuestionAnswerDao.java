package com.forcetower.uefs.db_service.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.forcetower.uefs.db_service.entity.QuestionAnswer;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

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
