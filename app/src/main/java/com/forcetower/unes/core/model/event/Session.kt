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
import com.google.gson.annotations.SerializedName
import org.threeten.bp.ZonedDateTime
import java.util.*

@Entity(indices = [
    Index(value = ["uuid"], unique = true)
])
data class Session(
    @SerializedName(value = "id")
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0,
    @ColumnInfo(name = "day_id")
    var day: Int = 0,
    @ColumnInfo(name = "start_time")
    @SerializedName("start_time")
    var startTime: ZonedDateTime = ZonedDateTime.now(),
    @ColumnInfo(name = "end_time")
    @SerializedName("end_time")
    var endTime: ZonedDateTime = ZonedDateTime.now(),
    var title: String = "",
    var room: String = "",
    var abstract: String = "",
    @SerializedName("photo_url")
    var photoUrl: String = "",
    var uuid: String = ""
): Comparable<Session> {

    @Ignore
    val year = startTime.year
    @Ignore
    val duration = endTime.toInstant().toEpochMilli() - startTime.toInstant().toEpochMilli()

    fun isLive(): Boolean {
        val now = ZonedDateTime.now()
        return startTime <= now && endTime >= now
    }

    fun isOverlapping(session: Session): Boolean {
        return this.startTime < session.endTime && this.endTime > session.startTime
    }

    override fun compareTo(other: Session): Int {
        val value = startTime.compareTo(other.startTime)
        return if (value != 0)
            value
        else
            duration.compareTo(other.duration)
    }
}