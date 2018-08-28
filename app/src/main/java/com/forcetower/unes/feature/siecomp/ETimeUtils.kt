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

package com.forcetower.unes.feature.siecomp

import com.forcetower.unes.BuildConfig
import com.forcetower.unes.core.model.event.EventDay
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime

object ETimeUtils {
    val SIECOMP_TIMEZONE: ZoneId = ZoneId.of(BuildConfig.SIECOMP_TIMEZONE)
    val SIECOMPDays = listOf(
            EventDay(
                    0,
                    ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY1_START),
                    ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY1_END)
            ),
            EventDay(
                    1,
                    ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY2_START),
                    ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY2_END)
            ),
            EventDay(
                    2,
                    ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY3_START),
                    ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY3_END)
            ),
            EventDay(
                    3,
                    ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY4_START),
                    ZonedDateTime.parse(BuildConfig.CONFERENCE_DAY4_END)
            )
    )

    fun zonedTime(time: ZonedDateTime, zoneId: ZoneId = ZoneId.systemDefault()): ZonedDateTime {
        return ZonedDateTime.ofInstant(time.toInstant(), zoneId)
    }
}