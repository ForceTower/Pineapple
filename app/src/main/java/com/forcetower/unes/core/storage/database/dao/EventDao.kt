/*
 * Copyright (c) 2018.
 * João Paulo Sena <joaopaulo761@gmail.com>
 *
 * This file is part of the UNES Open Source Project.
 *
 * UNES is licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.forcetower.unes.core.storage.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.forcetower.unes.core.model.event.*
import com.forcetower.unes.core.storage.database.accessors.SessionWithData
import com.forcetower.unes.core.storage.network.ServerSession
import timber.log.Timber

@Dao
abstract class EventDao {
    @Insert(onConflict = REPLACE)
    abstract fun insert(session: Session): Long

    @Transaction
    @Query("SELECT * FROM Session WHERE day_id = :day ORDER BY start_time ASC")
    abstract fun getSessionsFromDay(day: Int): LiveData<List<SessionWithData>>

    @Transaction
    open fun insertServerSessions(value: List<ServerSession>) {
        Timber.d("Requested insert of $value")
        value.forEach {
            insertTags(it.tags)
            insertSpeakers(it.speakers)
            val session = it.toSession()
            deleteIfPresent(session.uuid)
            val id = insert(session)

            it.tags.forEach { t ->
                assocTag(SessionTag(session = id, tag = t.uid))
            }

            it.speakers.forEach { s ->
                assocSpeaker(SessionSpeaker(session = id, speaker = s.uid))
            }
        }
    }



    @Query("SELECT * FROM Session WHERE uuid = :uuid")
    protected abstract fun getSessionWithUUID(uuid: String): Session?

    @Insert(onConflict = REPLACE)
    protected abstract fun insertTags(tags: List<Tag>)

    @Insert(onConflict = REPLACE)
    protected abstract fun insertSpeakers(speakers: List<Speaker>)

    @Insert(onConflict = REPLACE)
    protected abstract fun assocTag(tagged: SessionTag)

    @Insert(onConflict = REPLACE)
    protected abstract fun assocSpeaker(speaker: SessionSpeaker)

    @Query("DELETE FROM Session WHERE uuid = :sessionUUID")
    protected abstract fun deleteIfPresent(sessionUUID: String)
}