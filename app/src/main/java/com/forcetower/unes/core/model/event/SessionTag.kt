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
import androidx.room.ForeignKey.CASCADE
import java.util.*

@Entity(foreignKeys = [
    ForeignKey(entity = Session::class, parentColumns = ["uid"], childColumns = ["session_id"], onDelete = CASCADE, onUpdate = CASCADE),
    ForeignKey(entity = Tag::class, parentColumns = ["uid"], childColumns = ["tag_id"], onUpdate = CASCADE, onDelete = CASCADE)
], indices = [
    Index(value = ["session_id", "tag_id"], unique = true),
    Index(value = ["session_id"]),
    Index(value = ["tag_id"]),
    Index(value = ["uuid"], unique = true)
])
data class SessionTag(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    @ColumnInfo(name = "session_id")
    val session: Long,
    @ColumnInfo(name = "tag_id")
    val tag: Long,
    val uuid: String = UUID.randomUUID().toString()
)