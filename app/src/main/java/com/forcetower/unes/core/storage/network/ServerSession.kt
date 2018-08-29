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

package com.forcetower.unes.core.storage.network

import com.forcetower.unes.core.model.event.*
import com.google.gson.annotations.SerializedName
import org.threeten.bp.ZonedDateTime

data class ServerSession(
    @SerializedName(value = "id")
    val uid: Long,
    val day: Int,
    @SerializedName("start_time")
    val startTime: ZonedDateTime,
    @SerializedName("end_time")
    val endTime: ZonedDateTime,
    val title: String,
    val room: String,
    val abstract: String,
    @SerializedName("photo_url")
    val photoUrl: String,
    val uuid: String,

    val tags: List<Tag>,
    val speakers: List<Speaker>
) {
    fun toSession() = Session(uid, day, startTime, endTime, title, room, abstract, photoUrl, uuid)
}