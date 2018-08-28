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

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

private const val formatPattern = "d MMMM"

@SuppressLint("ConstantLocale")
val FORMATTER_MONTH_DAY: DateTimeFormatter =
        DateTimeFormatter.ofPattern(formatPattern, Locale.getDefault())

data class EventDay(
    @PrimaryKey(autoGenerate = true)
    val uid: Long = 0,
    val start: ZonedDateTime,
    val end: ZonedDateTime
) {
    fun contains(session: Session) = start <= session.startTime && end >= session.endTime
    fun formatMonthDay(): String = FORMATTER_MONTH_DAY.format(start)
}