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

package com.forcetower.unes.feature.siecomp.day

import android.content.Context
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.forcetower.unes.R
import com.forcetower.unes.feature.siecomp.ETimeUtils
import org.threeten.bp.Duration
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

@BindingAdapter(
    "sessionStart",
    "sessionEnd",
    "sessionRoom",
    "timeZoneId",
    requireAll = true
) fun sessionDurationLocation(
        textView: TextView,
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
        room: String,
        timeZoneId: ZoneId?
) {
    val finalTimeZoneId = timeZoneId ?: ZoneId.systemDefault()
    val localStartTime = ETimeUtils.zonedTime(startTime, finalTimeZoneId)
    val localEndTime = ETimeUtils.zonedTime(endTime, finalTimeZoneId)

    textView.context.getString(
            R.string.event_session_duration_location,
            durationString(textView.context, Duration.between(startTime, endTime)), room
    )
}

private fun durationString(context: Context, duration: Duration): String {
    val hours = duration.toHours()
    return if (hours > 0L) {
        context.resources.getQuantityString(R.plurals.duration_hours, hours.toInt(), hours)
    } else {
        val minutes = duration.toMinutes()
        context.resources.getQuantityString(R.plurals.duration_minutes, minutes.toInt(), minutes)
    }
}