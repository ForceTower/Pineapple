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

package com.forcetower.unes.core.storage.database.accessors

import androidx.room.Embedded
import androidx.room.Relation
import com.forcetower.unes.core.model.event.Session
import com.forcetower.unes.core.model.event.SessionSpeaker
import com.forcetower.unes.core.model.event.SessionTag
import timber.log.Timber

class SessionWithData: Comparable<SessionWithData>{
    @Embedded
    lateinit var session: Session
    @Relation(entityColumn = "session_id", parentColumn = "uid", entity = SessionSpeaker::class)
    lateinit var speakersRel: List<SessionSpeakerTalker>
    @Relation(entityColumn = "session_id", parentColumn = "uid", entity = SessionTag::class)
    lateinit var displayTags: List<SessionTagged>

    fun tags() = displayTags.map { it.singleTag() }.also { Timber.d("Size is: ${it.size}") }
    fun speakers() = speakersRel.map { it.singleSpeaker() }

    override fun compareTo(other: SessionWithData): Int {
        return session.compareTo(other.session)
    }
}