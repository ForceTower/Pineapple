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

package com.forcetower.unes.core.model.event

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity(foreignKeys = [
    ForeignKey(entity = Session::class, parentColumns = ["uid"], childColumns = ["session_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE),
    ForeignKey(entity = Speaker::class, parentColumns = ["uid"], childColumns = ["speaker_id"], onDelete = ForeignKey.CASCADE, onUpdate = ForeignKey.CASCADE)
], indices = [
        Index(value = ["session_id", "speaker_id"], unique = true),
        Index(value = ["session_id"]),
        Index(value = ["speaker_id"]),
        Index(value = ["uuid"], unique = true)
])
data class SessionSpeaker(
    @SerializedName(value = "id")
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo(name = "session_id")
    val session: Long,
    @ColumnInfo(name = "speaker_id")
    val speaker: Long,
    val uuid: String = UUID.randomUUID().toString()
)