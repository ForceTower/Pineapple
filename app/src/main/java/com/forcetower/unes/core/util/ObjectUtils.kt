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

package com.forcetower.unes.core.util

import com.google.gson.JsonDeserializer
import com.google.gson.JsonParseException
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter


object ObjectUtils {
    @JvmStatic
    val ZDT_DESERIALIZER: JsonDeserializer<ZonedDateTime> = JsonDeserializer { json, _, _ ->
        val jsonPrimitive = json.asJsonPrimitive
        try {

            // if provided as String - '2011-12-03T10:15:30+01:00[Europe/Paris]'
            if (jsonPrimitive.isString) {
                return@JsonDeserializer ZonedDateTime.parse(jsonPrimitive.asString, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            }

            // if provided as Long
            if (jsonPrimitive.isNumber) {
                return@JsonDeserializer ZonedDateTime.ofInstant(Instant.ofEpochMilli(jsonPrimitive.asLong), ZoneId.systemDefault())
            }

        } catch (e: RuntimeException) {
            throw JsonParseException("Unable to parse ZonedDateTime", e)
        }

        throw JsonParseException("Unable to parse ZonedDateTime")
    }
}