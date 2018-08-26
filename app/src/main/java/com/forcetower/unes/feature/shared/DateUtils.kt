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

package com.forcetower.unes.feature.shared

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.forcetower.unes.R
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@BindingAdapter(value = ["timestamped"])
fun getTimeStampedDate(view: TextView, time: Long) {
    val context = view.context
    val now = System.currentTimeMillis()
    val diff = now - time

    val oneDay = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
    val oneHor = TimeUnit.MILLISECONDS.convert(1, TimeUnit.HOURS)
    val days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)
    val value = when {
        days > 1L -> {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val str = format.format(Date(time))
            context.getString(R.string.message_received_date_format, str)
        }
        days == 1L -> {
            val hours = TimeUnit.HOURS.convert(diff - oneDay, TimeUnit.MILLISECONDS)
            val str = days.toString() + "d " + hours.toString() + "h"
            context.getString(R.string.message_received_date_ago_format, str)
        }
        else -> {
            val hours = TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS)
            val minutes = TimeUnit.MINUTES.convert(diff - (hours*oneHor), TimeUnit.MILLISECONDS)
            val str = hours.toString() + "h " + minutes + "min"
            context.getString(R.string.message_received_date_ago_format, str)
        }
    }
    view.text = value
}

fun Int.toLongWeekDay(): String {
    return when (this) {
        1 -> "Domingo"
        2 -> "Segunda"
        3 -> "Terça"
        4 -> "Quarta"
        5 -> "Quinta"
        6 -> "Sexta"
        7 -> "Sábado"
        else -> "UNDEFINED"
    }
}

fun Int.toWeekDay(): String {
    return when (this) {
        1 -> "DOM"
        2 -> "SEG"
        3 -> "TER"
        4 -> "QUA"
        5 -> "QUI"
        6 -> "SEX"
        7 -> "SAB"
        else -> "UNDEFINED"
    }
}

fun String.fromWeekDay(): Int {
    return when(this.toUpperCase()) {
        "DOM" -> 1
        "SEG" -> 2
        "TER" -> 3
        "QUA" -> 4
        "QUI" -> 5
        "SEX" -> 6
        "SAB" -> 7
        else -> 0
    }
}